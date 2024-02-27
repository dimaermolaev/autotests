package ru.digital_spirit.qaaf.utils.types;

public class DocType {
    private final String docType;
    public DocType(String docType) {

        this.docType = docType;
    }

    @Override
    public String toString() {
        return docType;
    }
}
