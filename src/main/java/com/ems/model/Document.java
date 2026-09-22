package com.ems.model;

import java.time.LocalDateTime;

public class Document {

    private long documentId;
    private long employeeId;

    private String documentType;
    private String documentTitle;
    private String fileReference;
    private String description;

    private String status;
    private String rejectionReason;

    private Long reviewedBy;

    private LocalDateTime uploadedAt;
    private LocalDateTime reviewedAt;

    // Empty constructor
    public Document() {
    }

    // Constructor for creating a new document
    public Document(
            long employeeId,
            String documentType,
            String documentTitle,
            String fileReference,
            String description) {

        this.employeeId = employeeId;
        this.documentType = documentType;
        this.documentTitle = documentTitle;
        this.fileReference = fileReference;
        this.description = description;
        this.status = "PENDING";
    }

    // Full constructor
    public Document(
            long documentId,
            long employeeId,
            String documentType,
            String documentTitle,
            String fileReference,
            String description,
            String status,
            String rejectionReason,
            Long reviewedBy,
            LocalDateTime uploadedAt,
            LocalDateTime reviewedAt) {

        this.documentId = documentId;
        this.employeeId = employeeId;
        this.documentType = documentType;
        this.documentTitle = documentTitle;
        this.fileReference = fileReference;
        this.description = description;
        this.status = status;
        this.rejectionReason = rejectionReason;
        this.reviewedBy = reviewedBy;
        this.uploadedAt = uploadedAt;
        this.reviewedAt = reviewedAt;
    }

    public long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(long documentId) {
        this.documentId = documentId;
    }

    public long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(long employeeId) {
        this.employeeId = employeeId;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    public String getFileReference() {
        return fileReference;
    }

    public void setFileReference(String fileReference) {
        this.fileReference = fileReference;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public Long getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(Long reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    @Override
    public String toString() {
        return "Document{" +
                "documentId=" + documentId +
                ", employeeId=" + employeeId +
                ", documentType='" + documentType + '\'' +
                ", documentTitle='" + documentTitle + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
