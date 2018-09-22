package de.sopro.filter;

public class Views {
    public interface BasicCombination {}

    public interface DetailCombination extends BasicCombination, BasicProduct, BasicUser{}

    public interface BasicProduct{}

    public interface DetailProduct extends BasicProduct, DetailFormatVersion{}

    public interface BasicFormat{}

    public interface DetailFormat extends BasicFormat, BasicFormatVersion{}

    public interface BasicFormatVersion{}

    public interface DetailFormatVersion extends BasicFormatVersion, BasicFormat {}

    public interface DetailUser extends BasicUser{}

    public interface BasicUser {}
}
