package de.sopro.services;

import de.sopro.DTO.FormatDTO;
import de.sopro.model.CompatibilityDegree;
import de.sopro.model.Format;
import de.sopro.model.FormatVersion;
import de.sopro.model.Product;
import de.sopro.repository.FormatRepository;
import de.sopro.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.NameAlreadyBoundException;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class FormatService {

    //Used Services
    private final TransactionHelper transactionHelper;

    //Used Repositories
    private final FormatRepository formatRepository;

    private final ProductRepository productRepository;

    @Autowired
    public FormatService(TransactionHelper transactionHelper, FormatRepository formatRepository, ProductRepository productRepository) {
        this.transactionHelper = transactionHelper;
        this.formatRepository = formatRepository;
        this.productRepository = productRepository;
    }

    public Optional<Format> getFormat(String formatName) {
        return transactionHelper.withTransaction(() -> formatRepository.findFormatByName(formatName));
    }

    /**
     * Creates a Format with given Name and returns or returns the existing Format.
     *
     * @param formatName Name for the tag.
     * @return The tag to work with.
     */
    public Format createOrReturnFormat(String formatName, CompatibilityDegree compatibilityDegree) {
        Optional<Format> optionalFormat = getFormat(formatName);

        return optionalFormat.orElseGet(() -> createFormat(formatName, compatibilityDegree));
    }


    private Format createFormat(String name, CompatibilityDegree compatibilityDegree) {
        return transactionHelper.withTransaction(() -> {
            Format format = new Format();

            //set minimal fields
            format.setName(name);
            format.setCompatibilityDegree(compatibilityDegree);

            return formatRepository.save(format);
        });
    }

    public Optional<FormatVersion> getFormatVersion(String formatName, String versionName) {
        Optional<Format> optionalFormat = getFormat(formatName);

        if (!optionalFormat.isPresent()) {
            return Optional.empty();
        }

        return optionalFormat.get().getVersions().stream().filter(fv -> fv.getName().equals(versionName)).findFirst();
    }

    public FormatVersion createOrReturnFormatVersion(String formatName, CompatibilityDegree compatibilityDegree, String versionName) {
        Format format = createOrReturnFormat(formatName, compatibilityDegree);

        return transactionHelper.withTransaction(() -> {
            FormatVersion fv = format.addVersion(versionName);
            formatRepository.save(format);

            return fv;
        });
    }

    public Format createFormat(FormatDTO formatDTO) throws NameAlreadyBoundException {
        Format format = new Format();
        format.setName(formatDTO.getName());

        if (formatDTO.isFlexible()) {
            format.setCompatibilityDegree(CompatibilityDegree.FLEXIBLE);
        } else {
            format.setCompatibilityDegree(CompatibilityDegree.STRICT);
        }

        String versionString = formatDTO.getVersions().replace(" ", "");
        String[] versions = versionString.split(",");

        for (String version : versions) {
            format.addVersion(version);
        }

        formatRepository.save(format);

        return format;
    }

    public void addVersion(Format format, String versions) {
        versions = versions.replace(" ", "");
        for (String version : versions.split(",")) {
            format.addVersion(version);
            formatRepository.save(format);
        }
    }

    public void deleteFormat(int formatID) throws IllegalStateException, NoSuchElementException{
        Optional<Format> optionalFormat = formatRepository.findFormatById(formatID);

        if(!optionalFormat.isPresent()){
            throw new NoSuchElementException("Format not found.");
        }
        Format format = optionalFormat.get();

        for(FormatVersion formatVersion : format.getVersions()){
            if(!formatVersion.getFormatInProducts().isEmpty() ||
                    !formatVersion.getFormatOutProducts().isEmpty()){
                throw new IllegalStateException("Format is in use, so it can't be deleted");
            }
        }
        formatRepository.delete(format);
    }



}