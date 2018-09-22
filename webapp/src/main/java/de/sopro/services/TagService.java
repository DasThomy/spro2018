package de.sopro.services;

import de.sopro.model.Tag;
import de.sopro.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TagService {

    //Used Services
    private final TransactionHelper transactionHelper;

    //Used Repositories
    private final TagRepository tagRepository;

    @Autowired
    public TagService(TransactionHelper transactionHelper, TagRepository tagRepository) {
        this.transactionHelper = transactionHelper;
        this.tagRepository = tagRepository;
    }

    public Optional<Tag> getTag(String name) {
        return transactionHelper.withTransaction(() -> tagRepository.findById(name));
    }

    /**
     * Creates a Tag with given Name and returns or returns the existing Tag.
     * @param name Name for the tag.
     * @return The tag to work with.
     */
    public Tag createOrReturn(String name) {
        Optional<Tag> optionalTag =  getTag(name);

        return optionalTag.orElseGet(() -> transactionHelper.withTransaction(() -> createTag(name)));
    }

    private Tag createTag(String name) {
        Tag newTag = new Tag();
        newTag.setName(name);
        return tagRepository.save(newTag);
    }
}
