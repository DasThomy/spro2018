package de.sopro.DTO;

import de.sopro.model.CompatibilityDegree;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class ProductsJsonDTO {

    @NotEmpty
    List<ProductDTO> products;

    public List<ProductDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDTO> products) {
        this.products = products;
    }

    @Override
    public String toString() {
        return "ProductsJsonDTO{" +
                "products=" + products +
                '}';
    }

    public static class ProductDTO {
        @NotBlank
        private String name;
        private boolean certified;
        @NotBlank
        private String organisation;
        @NotNull
        private String version;

        private long date;
        private List<String> tags;
        private String logo;
        private List<FormatDTO> formatIn;
        private List<FormatDTO> formatOut;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isCertified() {
            return certified;
        }

        public void setCertified(boolean certified) {
            this.certified = certified;
        }

        public String getOrganisation() {
            return organisation;
        }

        public void setOrganisation(String organisation) {
            this.organisation = organisation;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public long getDate() {
            return date;
        }

        public void setDate(Long date) {
            this.date = date;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public List<FormatDTO> getFormatIn() {
            return formatIn;
        }

        public void setFormatIn(List<FormatDTO> formatIn) {
            this.formatIn = formatIn;
        }

        public List<FormatDTO> getFormatOut() {
            return formatOut;
        }

        public void setFormatOut(List<FormatDTO> formatOut) {
            this.formatOut = formatOut;
        }

        @Override
        public String toString() {
            return "ProductDTO{" +
                    "name='" + name + '\'' +
                    ", organisation='" + organisation + '\'' +
                    ", version='" + version + '\'' +
                    ", date=" + date +
                    ", tags=" + tags +
                    ", logo='" + logo + '\'' +
                    ", formatIn=" + formatIn +
                    ", formatOut=" + formatOut +
                    '}';
        }

        public static class FormatDTO {

            //-------------- Attributes ---------------
            @NotBlank
            private String type;
            @NotNull
            private CompatibilityDegree compatibilityDegree;
            private String version;

            //-------------- Getter/Setter ---------------
            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public CompatibilityDegree getCompatibilityDegree() {
                return compatibilityDegree;
            }

            public void setCompatibilityDegree(CompatibilityDegree compatibilityDegree) {
                this.compatibilityDegree = compatibilityDegree;
            }

            public String getVersion() {
                return version;
            }

            public void setVersion(String version) {
                this.version = version;
            }


            @Override
            public String toString() {
                return "FormatDTO{" +
                        "type='" + type + '\'' +
                        ", version='" + version + '\'' +
                        ", compatibilityDegree='" + compatibilityDegree + '\'' +
                        '}';
            }
        }
    }
}
