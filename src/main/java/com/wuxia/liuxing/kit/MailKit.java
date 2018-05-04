package com.wuxia.liuxing.kit;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;

import com.jfinal.kit.PropKit;

public class MailKit {
    private String hostname;
    private String username;
    private String password;
    private String fromname;
    private List<String> mailtoList = new ArrayList<String>();
    
    public MailKit() {
        this("mail.properties");
    }

    public MailKit(String configName) {
        this.hostname = PropKit.use(configName).get("default_host_name");
        this.username = PropKit.use(configName).get("default_user_name");
        this.password = PropKit.use(configName).get("default_pass_word");
        this.fromname = PropKit.use(configName).get("default_from_name");
    }
    
    public void mailto(String mail) {
        mailtoList.add(mail);
    }

    public void sendMail(String subject, String msg) throws EmailException {
        HtmlEmail email = new HtmlEmail();
        email.setHostName(hostname);
        email.setAuthentication(username, password);
        email.setFrom(username, fromname);
        email.setCharset("UTF-8");
        email.setSubject(subject);
        email.setMsg(msg.replaceAll("\t", "        "));// format msg
        email.setHtmlMsg(msg);
        for (String mailto : mailtoList) {
            email.addTo(mailto);
        }
        email.send();
    }
    
    public static void main(String[] args) throws Exception {
        MailKit mail = new MailKit();
        mail.mailto("syu1112@qq.com");
        mail.sendMail("监控标题", "监控内容");
    }
    
//
//    public void sendHtmlMail(String subject, String message) throws EmailException {
//        sendHtmlMail(subject, message, getCommonMailTos());
//    }
//
//    public void sendHtmlMail(String subject, String message, List<String> mailTos) throws EmailException {
//        HtmlEmail email = new HtmlEmail();
//        buildMail(email, subject, message, mailTos);
//        email.setHtmlMsg(message);
//        email.send();
//    }
//
//    /**
//     * 发送简单的邮件
//     *
//     * @param subject
//     *            主题
//     * @param message
//     *            邮件正文
//     * @throws EmailException
//     */
//    public void sendSimpleMail(String subject, String message) throws EmailException {
//        sendSimpleMail(subject, message, getCommonMailTos());
//    }
//
//    /**
//     * 发送简单的邮件并指定收件人
//     *
//     * @param subject
//     *            主题
//     * @param message
//     *            邮件正文
//     * @param mailTos
//     *            收件人
//     * @throws EmailException
//     */
//    public void sendSimpleMail(String subject, String message, List<String> mailTos) throws EmailException {
//        Email email = new SimpleEmail();
//        buildMail(email, subject, message, mailTos);
//        email.send();
//    }
//
//    /**
//     * 发送带有附件的邮件
//     *
//     * @param subject
//     *            主题
//     * @param message
//     *            邮件正文
//     * @param attachments
//     *            附件
//     * @throws EmailException
//     * @throws IOException
//     */
//    public void sendMultiPartMail(String subject, String message, List<File> attachments) throws EmailException, IOException {
//        sendMultiPartMail(subject, message, attachments, attachment);
//    }
//
//    /**
//     * 发送带有附件的邮件并指定附件压缩后的文件名称
//     *
//     * @param subject
//     *            主题
//     * @param message
//     *            邮件正文
//     * @param attachments
//     *            附件
//     * @param compressFileName
//     *            附件压缩后的文件名称
//     * @throws EmailException
//     * @throws IOException
//     */
//    public void sendMultiPartMail(String subject, String message, List<File> attachments, String compressFileName) throws EmailException, IOException {
//        sendMultiPartMail(subject, message, attachments, compressFileName, null);
//    }
//
//    /**
//     * 发送带有附件的邮件并指定收件人
//     *
//     * @param subject
//     *            主题
//     * @param message
//     *            邮件正文
//     * @param attachments
//     *            附件
//     * @param mailTos
//     *            收件人
//     * @throws EmailException
//     * @throws IOException
//     */
//    public void sendMultiPartMail(String subject, String message, List<File> attachments, List<String> mailTos) throws EmailException, IOException {
//        sendMultiPartMail(subject, message, attachments, attachment, mailTos);
//    }
//
//    /**
//     * 发送带有附件的邮件并指定收件人和指定附件压缩后的文件名称
//     *
//     * @param subject
//     *            主题
//     * @param message
//     *            邮件正文
//     * @param attachments
//     *            附件
//     * @param compressFileName
//     *            附件压缩后的文件名称
//     * @param mailTos
//     *            收件人
//     * @throws EmailException
//     * @throws IOException
//     */
//    public void sendMultiPartMail(String subject, String message, List<File> attachments, String compressFileName, List<String> mailTos) throws EmailException, IOException {
//        MultiPartEmail email = new MultiPartEmail();
//        buildMail(email, subject, message, mailTos);
//
//        File compressFile = new File(compressFileName);
//        // compress first.
//        CompressUtil.zipCompress(attachments, compressFile);
//
//        EmailAttachment attachment = new EmailAttachment();
//
//        attachment.setName(compressFile.getName());
//        attachment.setPath(compressFile.getPath());
//        attachment.setDisposition(EmailAttachment.ATTACHMENT);
//
//        email.attach(attachment);
//
//        email.send();
//    }
//
//    // ///////////////////////////////////////////////////////////////////////////////////////////////////////
//    // Internal Use
//    // ///////////////////////////////////////////////////////////////////////////////////////////////////////
//
//    public static void main(String[] args) throws Exception {
//        // SimpleDateFormat format = new
//        // SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        // sendSimpleMail("测试发邮件", "当前时间："+format.format(new Date()));
//        // sendHtmlMail("测试发邮件", "<h1>当前时间</h1>：" + format.format(new Date()));
//
//        List<String> mailTos = new ArrayList<String>();
//        mailTos.add("672468896@qq.com");
//
//        MailClient client = new MailClient("mail.properties");
//        client.sendHtmlMail("测试", "内容", mailTos);
//        System.out.println("done");
//    }

}
