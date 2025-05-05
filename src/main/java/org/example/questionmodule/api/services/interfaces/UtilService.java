package org.example.questionmodule.api.services.interfaces;

import com.google.common.collect.Multimap;
import org.example.questionmodule.api.entities.Concept;
import org.example.questionmodule.api.entities.Relation;
import vn.pipeline.Word;

import java.util.List;
import java.util.Map;

public interface UtilService {
    public boolean matchConcept(Concept concept, String wordForm);
    public boolean matchRelation(Relation relation, String wordForm);

    public Multimap<Integer, Concept> getConcept(List<Concept> concepts, Multimap<Integer, List<Word>> words);

    public Multimap<Integer, Relation> getRelation(List<Relation> relations, Multimap<Integer, List<Word>> words);

    Multimap<Integer, List<Word>> deleteDuplicateWord ( Multimap<Integer, List<Word>> multimap);
}
