package com.example.itsav_conect;

public class DataPart {
    private String fileName;
    private byte[] fileData;
    private String mimeType; // Tercero parámetro que probablemente falta

    public DataPart(String fileName, byte[] fileData, String mimeType) {
        this.fileName = fileName;
        this.fileData = fileData;
        this.mimeType = mimeType;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public String getMimeType() {
        return mimeType;
    }
}
