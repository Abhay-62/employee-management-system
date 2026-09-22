package com.ems.service;

import com.ems.dao.DocumentDAO;
import com.ems.dto.EmployeeDashboardData;
import com.ems.model.Document;
import com.ems.session.SessionManager;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class DocumentService {
    private static final long MAX=10L*1024*1024;
    private static final Set<String> TYPES=Set.of("EDUCATIONAL_CERTIFICATE","PROFESSIONAL_CERTIFICATE","IDENTITY_DOCUMENT","PREVIOUS_EXPERIENCE","OTHER");
    private static final Set<String> EXTS=Set.of(".pdf",".jpg",".jpeg",".png");
    private final DocumentDAO dao; private final DashboardService dashboard;

    public DocumentService(){this(new DocumentDAO(),new DashboardService());}
    public DocumentService(DocumentDAO dao,DashboardService dashboard){this.dao=dao;this.dashboard=dashboard;}

    public Document uploadForCurrentEmployee(String type,String title,String desc,File file){
        SessionManager s=SessionManager.getInstance();
        if(!s.isLoggedIn()||!"EMPLOYEE".equalsIgnoreCase(s.getCurrentRole())) throw new IllegalStateException("Employee login is required.");
        EmployeeDashboardData d=dashboard.getEmployeeDashboardData(s.getCurrentUserId());
        if(!d.hasProfile()||d.getEmployeeId()==null) throw new IllegalStateException("Your employee profile has not been created yet.");
        validate(type,title,file);
        String ext=extension(file.getName()).toLowerCase(Locale.ROOT);
        String name=UUID.randomUUID()+ext;
        Path dir=Path.of("data","employee-documents","employee_"+d.getEmployeeId()), target=dir.resolve(name);
        try{
            Files.createDirectories(dir); Files.copy(file.toPath(),target,StandardCopyOption.REPLACE_EXISTING);
            Document doc=new Document(d.getEmployeeId(),type,title.trim(),"data/employee-documents/employee_"+d.getEmployeeId()+"/"+name,desc==null?null:desc.trim());
            try{dao.createDocument(doc);return doc;}catch(Exception e){Files.deleteIfExists(target);throw e;}
        }catch(IOException e){throw new RuntimeException("Unable to store the selected document.",e);}
        catch(RuntimeException e){throw e;}catch(Exception e){throw new RuntimeException("Unable to save document.",e);}
    }

    public List<Document> getCurrentEmployeeDocuments(){
        SessionManager s=SessionManager.getInstance();
        if(!s.isLoggedIn()||!"EMPLOYEE".equalsIgnoreCase(s.getCurrentRole())) throw new IllegalStateException("Employee login is required.");
        EmployeeDashboardData d=dashboard.getEmployeeDashboardData(s.getCurrentUserId());
        if(!d.hasProfile()||d.getEmployeeId()==null)return List.of();
        try{return dao.findByEmployeeId(d.getEmployeeId());}catch(Exception e){throw new RuntimeException("Unable to load documents.",e);}
    }

    public List<Document> getPendingDocuments(){requireAdmin();try{return dao.findPendingDocuments();}catch(Exception e){throw new RuntimeException("Unable to load pending documents.",e);}}
    public Document getDocument(long id){requireAdmin();try{return dao.findById(id).orElse(null);}catch(Exception e){throw new RuntimeException("Unable to load document.",e);}}

    public void approve(long id){
        long a=requireAdmin();
        try{if(!dao.approveDocument(id,a))throw new IllegalStateException("Document is no longer pending.");}
        catch(IllegalStateException e){throw e;}catch(Exception e){throw new RuntimeException("Unable to approve document.",e);}
    }

    public void reject(long id,String reason){
        long a=requireAdmin();
        if(reason==null||reason.isBlank())throw new IllegalArgumentException("A rejection reason is required.");
        try{if(!dao.rejectDocument(id,a,reason.trim()))throw new IllegalStateException("Document is no longer pending.");}
        catch(IllegalStateException e){throw e;}catch(Exception e){throw new RuntimeException("Unable to reject document.",e);}
    }

    private void validate(String type,String title,File f){
        if(!TYPES.contains(type))throw new IllegalArgumentException("Please select a valid document type.");
        if(title==null||title.isBlank()||title.trim().length()>255)throw new IllegalArgumentException("Enter a valid document title.");
        if(f==null||!f.isFile())throw new IllegalArgumentException("Please select a document file.");
        if(f.length()==0||f.length()>MAX)throw new IllegalArgumentException("File must be between 1 byte and 10 MB.");
        String e=extension(f.getName()).toLowerCase(Locale.ROOT);
        if(!EXTS.contains(e))throw new IllegalArgumentException("Only PDF, JPG, JPEG and PNG files are allowed.");
        try{
            byte[] h=Files.readAllBytes(f.toPath());
            boolean ok=e.equals(".pdf")?h.length>=4&&h[0]=='%'&&h[1]=='P'&&h[2]=='D'&&h[3]=='F':
                    e.equals(".png")?h.length>=8&&(h[0]&255)==137&&h[1]==80&&h[2]==78&&h[3]==71:
                    h.length>=3&&(h[0]&255)==255&&(h[1]&255)==216&&(h[2]&255)==255;
            if(!ok)throw new IllegalArgumentException("The selected file does not match its extension.");
        }catch(IOException e1){throw new IllegalArgumentException("Unable to inspect the selected file.");}
    }

    private String extension(String n){int i=n.lastIndexOf('.');return i<0?"":n.substring(i);}
    private long requireAdmin(){
        SessionManager s=SessionManager.getInstance();
        if(!s.isLoggedIn()||!"ADMIN".equalsIgnoreCase(s.getCurrentRole()))throw new IllegalStateException("Administrator login is required.");
        Long id=s.getCurrentUserId();if(id==null)throw new IllegalStateException("Invalid administrator session.");return id;
    }
}