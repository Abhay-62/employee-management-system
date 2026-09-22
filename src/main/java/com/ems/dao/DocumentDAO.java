package com.ems.dao;

import com.ems.config.DatabaseConfig;
import com.ems.model.Document;
import java.sql.*;
import java.util.*;

public class DocumentDAO {
    public long createDocument(Document d) throws SQLException {
        String sql = "INSERT INTO documents(employee_id,document_type,document_title,file_reference,description,status) VALUES(?,?,?,?,?,'PENDING') RETURNING document_id,uploaded_at,status";
        try (Connection c=DatabaseConfig.getConnection(); PreparedStatement s=c.prepareStatement(sql)) {
            s.setLong(1,d.getEmployeeId()); s.setString(2,d.getDocumentType()); s.setString(3,d.getDocumentTitle());
            s.setString(4,d.getFileReference()); s.setString(5,d.getDescription());
            try(ResultSet r=s.executeQuery()) {
                if(r.next()) {
                    d.setDocumentId(r.getLong("document_id")); d.setStatus(r.getString("status"));
                    Timestamp ts=r.getTimestamp("uploaded_at"); if(ts!=null)d.setUploadedAt(ts.toLocalDateTime());
                    return d.getDocumentId();
                }
            }
        }
        throw new SQLException("Document insert failed.");
    }

    public List<Document> findByEmployeeId(long employeeId) throws SQLException {
        String sql="SELECT document_id,employee_id,document_type,document_title,file_reference,description,status,rejection_reason,reviewed_by,uploaded_at,reviewed_at FROM documents WHERE employee_id=? ORDER BY uploaded_at DESC";
        List<Document> out=new ArrayList<>();
        try(Connection c=DatabaseConfig.getConnection();PreparedStatement s=c.prepareStatement(sql)){
            s.setLong(1,employeeId);try(ResultSet r=s.executeQuery()){while(r.next())out.add(map(r));}
        } return out;
    }

    public List<Document> findPendingDocuments() throws SQLException {
        String sql="SELECT document_id,employee_id,document_type,document_title,file_reference,description,status,rejection_reason,reviewed_by,uploaded_at,reviewed_at FROM documents WHERE status='PENDING' ORDER BY uploaded_at ASC";
        List<Document> out=new ArrayList<>();
        try(Connection c=DatabaseConfig.getConnection();PreparedStatement s=c.prepareStatement(sql);ResultSet r=s.executeQuery()){while(r.next())out.add(map(r));}
        return out;
    }

    public Optional<Document> findById(long id) throws SQLException {
        String sql="SELECT document_id,employee_id,document_type,document_title,file_reference,description,status,rejection_reason,reviewed_by,uploaded_at,reviewed_at FROM documents WHERE document_id=?";
        try(Connection c=DatabaseConfig.getConnection();PreparedStatement s=c.prepareStatement(sql)){
            s.setLong(1,id);try(ResultSet r=s.executeQuery()){if(r.next())return Optional.of(map(r));}
        } return Optional.empty();
    }

    public boolean approveDocument(long id,long adminId) throws SQLException {
        String sql="UPDATE documents SET status='APPROVED',reviewed_by=?,reviewed_at=CURRENT_TIMESTAMP,rejection_reason=NULL WHERE document_id=? AND status='PENDING'";
        try(Connection c=DatabaseConfig.getConnection();PreparedStatement s=c.prepareStatement(sql)){
            s.setLong(1,adminId);s.setLong(2,id);return s.executeUpdate()==1;
        }
    }

    public boolean rejectDocument(long id,long adminId,String reason) throws SQLException {
        String sql="UPDATE documents SET status='REJECTED',reviewed_by=?,reviewed_at=CURRENT_TIMESTAMP,rejection_reason=? WHERE document_id=? AND status='PENDING'";
        try(Connection c=DatabaseConfig.getConnection();PreparedStatement s=c.prepareStatement(sql)){
            s.setLong(1,adminId);s.setString(2,reason);s.setLong(3,id);return s.executeUpdate()==1;
        }
    }

    private Document map(ResultSet r)throws SQLException {
        Timestamp u=r.getTimestamp("uploaded_at"),v=r.getTimestamp("reviewed_at");
        return new Document(r.getLong("document_id"),r.getLong("employee_id"),r.getString("document_type"),
                r.getString("document_title"),r.getString("file_reference"),r.getString("description"),
                r.getString("status"),r.getString("rejection_reason"),r.getObject("reviewed_by",Long.class),
                u==null?null:u.toLocalDateTime(),v==null?null:v.toLocalDateTime());
    }
}