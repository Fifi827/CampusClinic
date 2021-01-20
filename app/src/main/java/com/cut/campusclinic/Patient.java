package com.cut.campusclinic;

public class Patient extends User {
    private String pre_existing_condition;
    private String medicalAidName;
    private String medicalAidNr;

    public String getPre_existing_condition() {
        return pre_existing_condition;
    }

    public void setPre_existing_condition(String pre_existing_condition) {
        this.pre_existing_condition = pre_existing_condition;
    }

    public String getMedicalAidName() {
        return medicalAidName;
    }

    public void setMedicalAidName(String medicalAidName) {
        this.medicalAidName = medicalAidName;
    }

    public String getMedicalAidNr() {
        return medicalAidNr;
    }

    public void setMedicalAidNr(String medicalAidNr) {
        this.medicalAidNr = medicalAidNr;
    }
}
