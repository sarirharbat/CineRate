package ch.unil.doplab.cinerate.domain;

public enum Genre {
    Action("Action"),
    Adventure("Adventure"),
    Animation("Animation"),
    Comedy("Comedy"),
    Crime("Crime"),
    Documentary("Documentary"),
    Drama("Drama"),
    Family("Family"),
    Fantasy("Fantasy"),
    History("History"),
    Horror("Horror"),
    Music("Music"),
    Mystery("Mystery"),
    Romance("Romance"),
    ScienceFiction("Science Fiction"),
    TVMovie("TV Movie"),
    Thriller("Thriller"),
    War("War"),
    Western("Western");

    private String label;

    Genre(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static Genre fromLabel(String label) {
        for (Genre genre : values()) {
            if (genre.label.equalsIgnoreCase(label)) {
                return genre;
            }
        }
        throw new IllegalArgumentException("Unknown genre: " + label);
    }
}

