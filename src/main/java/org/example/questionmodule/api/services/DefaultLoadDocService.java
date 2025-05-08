package org.example.questionmodule.api.services;

import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.example.questionmodule.api.dtos.admin.ArticleDto;
import org.example.questionmodule.api.dtos.admin.ClauseDto;
import org.example.questionmodule.api.dtos.admin.LawDto;
import org.example.questionmodule.api.dtos.admin.PointDto;
import org.example.questionmodule.api.entities.*;
import org.example.questionmodule.api.repositories.*;
import org.example.questionmodule.api.services.interfaces.LoadDocService;
import org.example.questionmodule.api.services.mapper.*;
import org.example.questionmodule.utils.exceptions.DataNotFoundException;
import org.springframework.ai.document.Document;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.pipeline.Sentence;
import vn.pipeline.Word;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.sql.Date;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DefaultLoadDocService implements LoadDocService {

    private final ConceptRepository conceptRepository;
    private final RelationRepository relationRepository;
    private final TripletRepository tripletRepository;
    private final GraphKnowledgeRepository graphKnowledgeRepository;
    private final AsyncService asyncService;
    private final LawMapper lawMapper;
    private final TripletGraphRepository tripletGraphRepository;
    private final ArticleRepository articleRepository;
    private final ClauseRepository clauseRepository;
    private final PointRepository pointRepository;


    @Override
    public String loadDoc(MultipartFile file) throws IOException {
        System.out.println("Load");
        System.out.println("File name: " + file.getOriginalFilename());
        System.out.println("Size: " + file.getSize());
        System.out.println("Content type: " + file.getContentType());

        String text = readWordFile(file);
        System.out.println(text);

        List<Document> documents = parseLawText(text);

        for (Document doc : documents) {
            System.out.println("====== Document " + (doc.getId()) + " ======");
            System.out.println("Metadata: ");

            Map<String, Object> metadata = doc.getMetadata();
            for (Map.Entry<String, Object> entry : metadata.entrySet()) {
                System.out.println("- " + entry.getKey() + ": " + entry.getValue());
            }

            System.out.println(); // Dòng trống phân cách
        }


        try {
            asyncService.saveToDatabase(documents);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public LawDto onlyLoadDoc(MultipartFile file) throws IOException {
        String text = readWordFile(file);
        List<Document> documents = parseLawText(text);
        for (Document doc : documents) {
            System.out.println("====== Document " + (doc.getId()) + " ======");
            System.out.println("Metadata: ");

            Map<String, Object> metadata = doc.getMetadata();
            for (Map.Entry<String, Object> entry : metadata.entrySet()) {
                System.out.println("- " + entry.getKey() + ": " + entry.getValue());
            }

            System.out.println(); // Dòng trống phân cách
        }
        var law = asyncService.saveLawDoc(documents);
        return lawMapper.toAdminDto(law);
    }

    @Override
    @Transactional
    @Async
    public void createTripletOfArticle(String id){
        List<Concept> concepts = conceptRepository.findAllQuery();
        List<Relation> relations = relationRepository.findAllQuery();
        List<Triplet> existingTriplets = tripletRepository.findAllQuery();

        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(List.of("Article not found")));
        article.setHasGraph(true);
        GraphKnowledge gk = GraphKnowledge.builder().article(article).build();


        List<Triplet> triplets = new ArrayList<>(asyncService.process(article.getTitle(), concepts, relations, existingTriplets));
        if (article.getContent() != null) triplets.addAll(asyncService.process(article.getContent(), concepts, relations, existingTriplets));

        saveTripletGraph(gk, triplets);
        articleRepository.save(article);
    }

    @Override
    @Transactional
    @Async
    public void createTripletOfClause(String id){
        List<Concept> concepts = conceptRepository.findAllQuery();
        List<Relation> relations = relationRepository.findAllQuery();
        List<Triplet> existingTriplets = tripletRepository.findAllQuery();

        Clause clause = clauseRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(List.of("Clause not found")));
        clause.setHasGraph(true);
        GraphKnowledge gk = GraphKnowledge.builder().clause(clause).build();


        List<Triplet> triplets = asyncService.process(clause.getContent(), concepts, relations, existingTriplets);

       saveTripletGraph(gk, triplets);
       clauseRepository.save(clause);
    }

    @Override
    @Transactional
    @Async
    public void createTripletOfPoint(String id){
        List<Concept> concepts = conceptRepository.findAllQuery();
        List<Relation> relations = relationRepository.findAllQuery();
        List<Triplet> existingTriplets = tripletRepository.findAllQuery();

        Point point = pointRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(List.of("Point not found")));
        point.setHasGraph(true);
        GraphKnowledge gk = GraphKnowledge.builder().point(point).build();
        long start = System.currentTimeMillis();
        List<Triplet> triplets = asyncService.process(point.getContent(), concepts, relations, existingTriplets);
        System.out.println("Process time = " + (System.currentTimeMillis() - start) + " ms");
        saveTripletGraph(gk, triplets);
        pointRepository.save(point);
    }

    private void saveTripletGraph(GraphKnowledge graphKnowledge, List<Triplet> triplets){
        var gk = graphKnowledgeRepository.save(graphKnowledge);
        List<TripletGraph> tripletGraphs = new ArrayList<>();
        Set<String> existingKeys = new HashSet<>();
        for (int j = 0; j < triplets.size(); j++) {
            Triplet triplet = triplets.get(j);
            String key = gk.getId() + "-" + triplet.getId();
            if (!existingKeys.contains(key)) {
                TripletGraph tg = new TripletGraph();
                tg.setTriplet(triplet);
                tg.setGraphKnowledge(gk);
                tg.setIsRoot(j == 0);
                tripletGraphs.add(tg);
                existingKeys.add(key);
            }
        }
        tripletGraphRepository.saveAll(tripletGraphs);
    }

    public String readPdfContent(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream();
             PDDocument document = PDDocument.load(inputStream)) {

            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        }
    }

    public String readWordFile(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
//        if (filename == null) throw new IOException("Không có tên file");

        try (InputStream inputStream = file.getInputStream()) {
            if (filename.endsWith(".docx")) {
                try (XWPFDocument docx = new XWPFDocument(inputStream);
                     XWPFWordExtractor extractor = new XWPFWordExtractor(docx)) {
                    return extractor.getText();
                }
            } else if (filename.endsWith(".doc")) {
                try (HWPFDocument doc = new HWPFDocument(inputStream);
                     WordExtractor extractor = new WordExtractor(doc)) {
                    return extractor.getText();
                }
            } else {
                throw new IOException("Không hỗ trợ định dạng file: " + filename);
            }
        }
    }

    public List<Document> parseLawText(String fullText) {
        fullText = fullText.replaceAll("\\r\\n", "\n").replaceAll("\\r", "\n");

        List<Document> documents = new ArrayList<>();

        // 1. Đọc Luật số
        String luatSo = "";
        Matcher lawNumberMatcher = Pattern.compile("Luật số:\\s*(\\S+)").matcher(fullText);
        if (lawNumberMatcher.find()) {
            luatSo = lawNumberMatcher.group(1);
        }

        // 2. Đọc tên Luật (dòng có chữ "LUẬT ...")
        String tenLuat = "";
        Matcher lawTitleMatcher = Pattern.compile("(?m)^LUẬT\\s+([A-ZÀ-Ỹ\\s]+)$").matcher(fullText);
        if (lawTitleMatcher.find()) {
            tenLuat = "LUẬT " + lawTitleMatcher.group(1).trim();
        }

        if (Objects.equals(luatSo, "")) {
            Matcher decreeNumberMatcher = Pattern.compile("Số:\\s*(\\S+)").matcher(fullText);
            if (decreeNumberMatcher.find()) {
                luatSo = decreeNumberMatcher.group(1);
                Matcher decreeTitleMatcher = Pattern.compile("(?m)^NGHỊ ĐỊNH\\s+([A-ZÀ-Ỹ0-9\\s,–-]+)$").matcher(fullText);
                if (decreeTitleMatcher.find()) {
                    tenLuat = "NGHỊ ĐỊNH " + decreeTitleMatcher.group(1).trim();
                }
            }
        }

        // 3. Đọc tên chương hiện tại
        Pattern chapterPattern = Pattern.compile("(?m)^(Chương\\s+[IVXLCDM]+)\\s+(.*)$");
        Matcher chapterMatcher = chapterPattern.matcher(fullText);

        TreeMap<Integer, String[]> chapterPositions = new TreeMap<>();
        while (chapterMatcher.find()) {
            String chapterCode = chapterMatcher.group(1).trim();     // Chương I
            String chapterTitle = chapterMatcher.group(2).trim();    // QUY ĐỊNH CHUNG
            chapterPositions.put(chapterMatcher.start(), new String[]{chapterCode, chapterTitle});
        }

        // 4. Tách các điều
        Pattern articlePattern = Pattern.compile("(Điều\\s+\\d+\\.\\s+.*?)(?=\\nĐiều\\s+\\d+\\.|\\Z)", Pattern.DOTALL);
        Matcher articleMatcher = articlePattern.matcher(fullText);

        while (articleMatcher.find()) {
            String articleBlock = articleMatcher.group(1).trim();
            int articleStart = articleMatcher.start();

            // Tìm chương gần nhất
            String chapterCode = "", chapterTitle = "";
            Map.Entry<Integer, String[]> nearestChapter = chapterPositions.floorEntry(articleStart);
            if (nearestChapter != null) {
                chapterCode = nearestChapter.getValue()[0];
                chapterTitle = nearestChapter.getValue()[1];
            }

            // Tiêu đề điều
            Matcher titleMatcher = Pattern.compile("Điều\\s+(\\d+)\\.\\s*(.*?)\\n").matcher(articleBlock);
            String articleNumber = "", articleTitle = "";
            if (titleMatcher.find()) {
                articleNumber = titleMatcher.group(1);
                articleTitle = titleMatcher.group(2);
            }

            // Khoản
            Pattern clausePattern = Pattern.compile("(?m)^\\d+\\.\\s+.*?(?=^\\d+\\.|\\Z)", Pattern.DOTALL);
            Matcher clauseMatcher = clausePattern.matcher(articleBlock);
            int clauseCount = 0;

            while (clauseMatcher.find()) {
                String rawClauseText = clauseMatcher.group().trim();
                clauseCount++;
                String clauseNumber = String.valueOf(clauseCount);

                // Bỏ số thứ tự ở đầu dòng clause (ví dụ "3. ")
                String clauseText = rawClauseText.replaceFirst("^\\d+\\.\\s*", "");

                // Nếu có dòng bắt đầu bằng "Chương", cắt tại đó
                Pattern chapterLinePattern = Pattern.compile("(?m)^Chương\\s+[IVXLCDM]+\\s+.*$");
                Matcher chapterLineMatcher = chapterLinePattern.matcher(clauseText);
                if (chapterLineMatcher.find()) {
                    clauseText = clauseText.substring(0, chapterLineMatcher.start()).trim();
                }

                // Nếu có điểm (a), b), ...), thì clauseContent là phần trước điểm đầu tiên
                int indexOfPoint = clauseText.indexOf("\na)");
                String clauseContent = (indexOfPoint != -1) ? clauseText.substring(0, indexOfPoint).trim() : clauseText;

                // Điểm
                Pattern pointPattern = Pattern.compile("(?m)^([a-z])\\)\\s+.*?(?=^\\w\\)|\\Z)", Pattern.DOTALL);
                Matcher pointMatcher = pointPattern.matcher(clauseText);

                boolean hasPoint = pointMatcher.find();
                if (hasPoint) {
                    pointMatcher.reset(); // Quay lại từ đầu
                    while (pointMatcher.find()) {
                        String pointLetter = pointMatcher.group(1);
                        String pointText = pointMatcher.group().trim();

                        // Bỏ chữ a), b), ... khỏi pointContent
                        String pointContent = pointText.replaceFirst("^[a-z]\\)\\s*", "");

                        Map<String, Object> metadata = new HashMap<>();
                        metadata.put("article", articleNumber);
                        metadata.put("title", articleTitle);
                        metadata.put("clause", clauseNumber);
                        metadata.put("clauseContent", clauseContent);
                        metadata.put("point", pointLetter);
                        metadata.put("pointContent", pointContent);
                        metadata.put("chapterCode", chapterCode);
                        metadata.put("chapterTitle", chapterTitle);
                        metadata.put("lawNumber", luatSo);
                        metadata.put("lawTitle", tenLuat);
                        metadata.put("fullArticle", false);

                        documents.add(new Document(pointText, metadata));
                    }
                } else {
                    // Không có điểm → lưu nguyên clause
                    Map<String, Object> metadata = new HashMap<>();
                    metadata.put("article", articleNumber);
                    metadata.put("title", articleTitle);
                    metadata.put("clause", clauseNumber);
                    metadata.put("clauseContent", clauseContent);
                    metadata.put("chapterCode", chapterCode);
                    metadata.put("chapterTitle", chapterTitle);
                    metadata.put("lawNumber", luatSo);
                    metadata.put("lawTitle", tenLuat);
                    metadata.put("fullArticle", false);

                    documents.add(new Document(clauseContent, metadata));
                }
            }


            // Không có khoản → lưu cả điều
            if (clauseCount == 0) {
                // Bỏ tiêu đề "Điều x. ..." để lấy nội dung
                String content = articleBlock.replaceFirst("Điều\\s+" + articleNumber + "\\.\\s*" + Pattern.quote(articleTitle), "").trim();

                Map<String, Object> metadata = new HashMap<>();
                metadata.put("article", articleNumber);
                metadata.put("title", articleTitle);
                metadata.put("chapterCode", chapterCode);
                metadata.put("chapterTitle", chapterTitle);
                metadata.put("lawNumber", luatSo);
                metadata.put("lawTitle", tenLuat);
                metadata.put("fullArticle", true);
                metadata.put("content", content);

                documents.add(new Document(content, metadata));
            }
        }

        return documents;
    }

}
