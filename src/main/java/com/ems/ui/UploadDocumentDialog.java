package com.ems.ui;
import com.ems.service.DocumentService;
import javax.swing.*;import java.awt.*;import java.io.File;
public class UploadDocumentDialog extends JDialog{
 private final DocumentService service=new DocumentService(); private JComboBox<String> type; private JTextField title,fileName; private JTextArea desc; private File file;
 public UploadDocumentDialog(JFrame owner,Runnable refresh){super(owner,"Upload Document",true);setSize(560,460);setLocationRelativeTo(owner);
  JPanel p=new JPanel();p.setBorder(BorderFactory.createEmptyBorder(22,25,22,25));p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
  JLabel h=new JLabel("Submit Document");h.setFont(new Font("Segoe UI",Font.BOLD,22));p.add(h);p.add(Box.createVerticalStrut(15));
  p.add(new JLabel("Document Type"));type=new JComboBox<>(new String[]{"EDUCATIONAL_CERTIFICATE","PROFESSIONAL_CERTIFICATE","IDENTITY_DOCUMENT","PREVIOUS_EXPERIENCE","OTHER"});p.add(type);
  p.add(Box.createVerticalStrut(10));p.add(new JLabel("Document Title"));title=new JTextField();p.add(title);
  p.add(Box.createVerticalStrut(10));p.add(new JLabel("Description"));desc=new JTextArea(4,30);desc.setLineWrap(true);p.add(new JScrollPane(desc));
  p.add(Box.createVerticalStrut(10));p.add(new JLabel("File"));JPanel fp=new JPanel(new BorderLayout(6,0));fileName=new JTextField();fileName.setEditable(false);JButton choose=new JButton("Choose");choose.addActionListener(e->choose());fp.add(fileName);fp.add(choose,BorderLayout.EAST);p.add(fp);
  p.add(Box.createVerticalStrut(15));JPanel a=new JPanel(new FlowLayout(FlowLayout.RIGHT));JButton cancel=new JButton("Cancel"),up=new JButton("Upload");up.setBackground(new Color(37,99,235));up.setForeground(Color.WHITE);cancel.addActionListener(e->dispose());up.addActionListener(e->{try{service.uploadForCurrentEmployee((String)type.getSelectedItem(),title.getText(),desc.getText(),file);JOptionPane.showMessageDialog(this,"Uploaded. Status: PENDING");if(refresh!=null)refresh.run();dispose();}catch(Exception x){JOptionPane.showMessageDialog(this,x.getMessage(),"Upload Error",JOptionPane.WARNING_MESSAGE);}});a.add(cancel);a.add(up);p.add(a);setContentPane(p);
 }
 private void choose(){JFileChooser c=new JFileChooser();c.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF/JPG/JPEG/PNG","pdf","jpg","jpeg","png"));if(c.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){file=c.getSelectedFile();fileName.setText(file.getName());}}
}