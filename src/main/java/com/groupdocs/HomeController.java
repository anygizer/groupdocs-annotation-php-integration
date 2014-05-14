package com.groupdocs;

import com.google.gson.Gson;
import com.groupdocs.annotation.config.ServiceConfiguration;
import com.groupdocs.annotation.domain.AccessRights;
import com.groupdocs.annotation.handler.AnnotationHandler;
import com.groupdocs.annotation.handler.GroupDocsAnnotation;
import com.groupdocs.config.ApplicationConfig;
import com.groupdocs.viewer.domain.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * User: liosha
 * Date: 05.12.13
 * Time: 22:54
 */
@Controller
public class HomeController extends GroupDocsAnnotation {
    @Autowired
    protected ApplicationConfig applicationConfig;
    protected AnnotationHandler annotationHandler = null;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Model model, HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "userName", required = false) String userName) throws Exception {
        return index(model, request, response, null, null, "/files/GroupDocs_Demo.doc", null, userName);
    }

    @RequestMapping(value = "/view", method = RequestMethod.GET)
    public String index(Model model, HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "fileId", required = false) String fileId, @RequestParam(value = "fileUrl", required = false) String fileUrl, @RequestParam(value = "filePath", required = false) String filePath, @RequestParam(value = "tokenId", required = false) String tokenId, @RequestParam(value = "userName", required = false) final String userName) throws Exception {
        if (annotationHandler == null) {
            TimeZone.setDefault(TimeZone.getTimeZone("Europe/Vilnius"));
            // Application path
            String appPath = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
            // File storage path
            String basePath = applicationConfig.getBasePath();
            // File license path
            String licensePath = applicationConfig.getLicensePath();
            // INITIALIZE GroupDocs Java Annotation Object
            ServiceConfiguration config = new ServiceConfiguration(appPath, basePath, licensePath, Boolean.FALSE, applicationConfig.getWidth());
            annotationHandler = new AnnotationHandler(config);
            // InputDataHandler.setInputDataHandler(new CustomInputDataHandler(config));
        }
        // Setting header in jsp page
        model.addAttribute("groupdocsHeader", annotationHandler.getHeader());
        // Initialization of Viewer with document from this path
        final GroupDocsPath groupDocsFilePath;

        if(fileId !=null && !fileId.isEmpty()){
            groupDocsFilePath = new FileId(fileId);
        }else if(filePath != null && !filePath.isEmpty()){
            groupDocsFilePath = new FilePath(filePath, annotationHandler.getConfiguration());
        }else if(fileUrl != null && !fileUrl.isEmpty()){
            groupDocsFilePath = new FileUrl(fileUrl);
        }else if(tokenId != null && !tokenId.isEmpty()){
            TokenId tki = new TokenId(tokenId);
            if(tki.isExpired()){
                groupDocsFilePath = null;
            }else{
                groupDocsFilePath = tki;
            }
        } else {
            groupDocsFilePath = null;
        }
        final String userGuid = annotationHandler.addCollaborator(userName, groupDocsFilePath.getPath(), AccessRights.All, getIntFromColor(Color.black));
        HashMap<String, String> params = new HashMap<String, String>() {{
            // You can skip parameters which have default value
            put("filePath",                             groupDocsFilePath.getPath()); // Default value: empty string
            put("width",                                Integer.toString(applicationConfig.getWidth()));            // Default value: 800
            put("height",                               Integer.toString(applicationConfig.getHeight()));           // Default value: 600
            put("quality",                              "75");              // Default value: 90
            put("enableRightClickMenu",                 "true");            // Default value: true
            put("showHeader",                           Boolean.toString(applicationConfig.getShowHeader()));       // Default value: true
            put("showZoom",                             Boolean.toString(applicationConfig.getShowZoom()));         // Default value: true
            put("showPaging",                           Boolean.toString(applicationConfig.getShowPaging()));       // Default value: true
            put("showPrint",                            Boolean.toString(applicationConfig.getShowPrint()));        // Default value: false
            put("showFileExplorer",                     "true");            // Default value: true
            put("showThumbnails",                       Boolean.toString(applicationConfig.getShowThumbnails()));   // Default value: true
            put("openThumbnails",                       Boolean.toString(applicationConfig.getOpenThumbnails()));   // Default value: false
            put("zoomToFitWidth",                       "false");           // Default value: false
            put("zoomToFitHeight",                      "false");           // Default value: false
            put("initialZoom",                          "100");             // Default value: 100
            put("preloadPagesCount",                    "0");               // Default value: 0
            put("enableSidePanel",                      "false");            // Default value: true
            put("strikeOutColor",                       "");                // Default value: empty string
            put("enabledTools",                         "255");             // Default value: 255
            put("saveReplyOnFocusLoss",                 "false");           // Default value: false
            put("strikeoutMode",                        "0");               // Default value: 0
            put("sideboarContainerSelector",            "div.comments_sidebar_wrapper"); // Default value: div.comments_sidebar_wrapper
            put("usePageNumberInUrlHash",               "false");           // Default value: false
            put("textSelectionSynchronousCalculation",  "true");            // Default value: true
            put("variableHeightPageSupport",            "true");            // Default value: true
            put("useJavaScriptDocumentDescription",     "true");            // Default value: true
            put("isRightPanelEnabled",                  "false");            // Default value: true
            put("createMarkup",                         "true");            // Default value: true
            put("use_pdf",                              "true");            // Default value: true
            put("_mode",                                "annotatedDocument");           // Default value: annotatedDocument
            put("selectionContainerSelector",           "[name='selection-content']");  // Default value: [name='selection-content']
            put("graphicsContainerSelector",            ".annotationsContainer");       // Default value: .annotationsContainer
            put("widgetId",                             "annotation-widget");           // Default value: annotation-widget
            put("userName",                             userName == null ? "Anonimous" : userName);
            put("userGuid",                             userGuid);
//            put("showFolderBrowser", Boolean.toString(applicationConfig.getShowFolderBrowser())); // Not used
//            put("showDownload", Boolean.toString(applicationConfig.getShowDownload())); // Not used
//            put("showSearch", Boolean.toString(applicationConfig.getShowSearch())); // Not used
        }};
        model.addAttribute("groupdocsScripts", annotationHandler.getScripts(params));
        model.addAttribute("width", applicationConfig.getWidth());   // It is for sample JSP (index.jsp)
        model.addAttribute("height", applicationConfig.getHeight()); // It is for sample JSP (index.jsp)

        return "index";
    }

    /**
     * Get JavaScript file [GET request]
     *
     * @param script
     * @param response
     * @throws IOException
     */
    @Override
    @RequestMapping(value = GET_JS_HANDLER, method = RequestMethod.GET)
    public void getJsHandler(String script, HttpServletResponse response) throws IOException {
        annotationHandler.getJsHandler(script, response);
    }

    /**
     * Get css file [GET request]
     *
     * @param script
     * @param response
     * @throws IOException
     */
    @Override
    @RequestMapping(value = GET_CSS_HANDLER, method = RequestMethod.GET)
    public void getCssHandler(String script, HttpServletResponse response) throws IOException {
        annotationHandler.getCssHandler(script, response);
    }

    /**
     * Get image file [GET request]
     *
     * @param name
     * @param response
     * @throws IOException
     */
    @Override
    @RequestMapping(value = GET_IMAGE_HANDLER, method = RequestMethod.GET)
    public void getImageHandler(@PathVariable String name, HttpServletResponse response) throws IOException {
        annotationHandler.getImageHandler(name, response);
    }

    @Override
    @RequestMapping(value = GET_FONT_HANDLER, method = RequestMethod.GET)
    public void getFontHandler(String name, HttpServletResponse response) throws IOException {
        annotationHandler.getFontHandler(name, response);
    }

    @Override
    @RequestMapping(value = GET_HTML_RESOURCES_HANDLER, method = RequestMethod.GET)
    public void getHtmlRecoucesHandler(String filePath, HttpServletResponse response) throws IOException {
        annotationHandler.getHtmlRecoucesHandler(filePath, response);
    }

    /**
     * Download file [GET request]
     *
     * @param path
     * @param response
     * @throws java.lang.Exception
     */
    @Override
    @RequestMapping(value = GET_FILE_HANDLER, method = RequestMethod.GET)
    public void getFileHandler(@RequestParam("path") String path, HttpServletResponse response) throws Exception {
        annotationHandler.getFileHandler(path, response);
    }

    /**
     * Get document image files [GET request]
     *
     * @param guid
     * @param width
     * @param quality
     * @param usePdf
     * @param pageIndex
     * @throws Exception
     */
    @Override
    @RequestMapping(value = GET_DOCUMENT_PAGE_IMAGE_HANDLER, method = RequestMethod.GET)
    public void getDocumentPageImageHandler(@RequestParam("path") String guid, @RequestParam("width") Integer width, @RequestParam("quality") Integer quality,
                                            @RequestParam("usePdf") Boolean usePdf, @RequestParam("pageIndex") Integer pageIndex, HttpServletResponse response) throws Exception {
        annotationHandler.getDocumentPageImageHandler(guid, width, quality, usePdf, pageIndex, response);
    }

    /**
     * Generate list of images/pages [POST request]
     *
     * @param request
     * @return
     */
    @Override
    @RequestMapping(value = VIEW_DOCUMENT_HANDLER, method = RequestMethod.POST)
    public ResponseEntity<String> viewDocumentHandler(HttpServletRequest request, HttpServletResponse response) {
        return jsonOut(annotationHandler.viewDocumentHandler(request, response));
    }

    /**
     * Generate list of images/pages [GET request]
     *
     * @param callback
     * @param data
     * @param request
     * @return
     */
    @Override
    @RequestMapping(value = VIEW_DOCUMENT_HANDLER, method = RequestMethod.GET)
    public ResponseEntity<String> viewDocumentHandler(String callback, String data, HttpServletRequest request, HttpServletResponse response) {
        return jsonOut(annotationHandler.viewDocumentHandler(callback, data, request, response));
    }

    /**
     * Load tree of files from base directory [POST request]
     *
     * @param request
     * @return
     */
    @Override
    @RequestMapping(value = LOAD_FILE_BROWSER_TREE_DATA_HANLER, method = RequestMethod.POST)
    public ResponseEntity<String> loadFileBrowserTreeDataHandler(HttpServletRequest request, HttpServletResponse response) {
        return jsonOut(annotationHandler.loadFileBrowserTreeDataHandler(request, response));
    }

    /**
     * Load tree of files from base directory [GET request]
     *
     * @param callback
     * @param data
     * @return
     */
    @Override
    @RequestMapping(value = LOAD_FILE_BROWSER_TREE_DATA_HANLER, method = RequestMethod.GET)
    public ResponseEntity<String> loadFileBrowserTreeDataHandler(String callback, String data, HttpServletResponse response) {
        return jsonOut(annotationHandler.loadFileBrowserTreeDataHandler(callback, data, response));
    }

    /**
     * Get thumbs and other images files [POST request]
     *
     * @param request
     * @return
     */
    @Override
    @RequestMapping(value = GET_IMAGE_URL_HANDLER, method = RequestMethod.POST)
    public ResponseEntity<String> getImageUrlsHandler(HttpServletRequest request, HttpServletResponse response) {
        return jsonOut(new Gson().toJson(annotationHandler.getImageUrlsHandler(request, response)));
    }

    /**
     * Get thumbs and other images files [GET request]
     *
     * @param callback
     * @param data
     * @param request
     * @return
     */
    @Override
    @RequestMapping(value = GET_IMAGE_URL_HANDLER, method = RequestMethod.GET)
    public ResponseEntity<String> getImageUrlsHandler(String callback, String data, HttpServletRequest request, HttpServletResponse response) {
        return jsonOut(annotationHandler.getImageUrlsHandler(callback, data, request, response));
    }

    /**
     * Converts document to PDF and then to JavaScript for text search and selection [POST request]
     *
     * @param request
     * @return
     */
    @Override
    @RequestMapping(value = GET_PDF_2_JAVA_SCRIPT_HANDLER, method = RequestMethod.POST)
    public ResponseEntity<String> getPdf2JavaScriptHandler(HttpServletRequest request, HttpServletResponse response) {
        return jsonOut(annotationHandler.getPdf2JavaScriptHandler(request, response));
    }

    /**
     * Converts document to PDF and then to JavaScript for text search and selection [GET request]
     *
     * @param callback
     * @param data
     * @return
     */
    @Override
    @RequestMapping(value = GET_PDF_2_JAVA_SCRIPT_HANDLER, method = RequestMethod.GET)
    public ResponseEntity<String> getPdf2JavaScriptHandler(String callback, String data, HttpServletResponse response) {
        return jsonOut(annotationHandler.getPdf2JavaScriptHandler(callback, data, response));
    }

    /**
     * Print document [POST request]
     *
     * @param request
     * @return
     */
    @Override
    @RequestMapping(value = GET_PRINTABLE_HTML_HANDLER, method = RequestMethod.POST)
    public ResponseEntity<String> getPrintableHtmlHandler(HttpServletRequest request, HttpServletResponse response) {
        return typeOut(annotationHandler.getPrintableHtmlHandler(request, response), MediaType.TEXT_HTML);
    }

    /**
     * Print document [GET request]
     *
     * @param callback
     * @param data
     * @param request
     * @return
     */
    @Override
    @RequestMapping(value = GET_PRINTABLE_HTML_HANDLER, method = RequestMethod.GET)
    public ResponseEntity<String> getPrintableHtmlHandler(String callback, String data, HttpServletRequest request, HttpServletResponse response) {
        return jsonOut(annotationHandler.getPrintableHtmlHandler(callback, data, request, response));
    }

    @Override
    @RequestMapping(value = GET_DOCUMENT_PAGE_HTML_HANDLER, method = RequestMethod.GET)
    public void getDocumentPageHtmlHandler(HttpServletRequest request, HttpServletResponse response) {
        annotationHandler.getDocumentPageHtmlHandler(request, response);
    }

    /**
     * Get list of annotations for document [POST request]
     *
     * @param request HTTP servlet request
     * @return
     */
    @Override
    @RequestMapping(value = LIST_ANNOTATIONS_HANDLER, method = RequestMethod.POST)
    public Object listAnnotationsHandler(HttpServletRequest request, HttpServletResponse response) {
        return jsonOut(annotationHandler.listAnnotationsHandler(request, response));
    }

    /**
     * Download document with annotations [POST request]
     *
     * @param request HTTP servlet request
     * @return
     */
    @Override
    @RequestMapping(value = EXPORT_ANNOTATIONS_HANDLER, method = RequestMethod.POST)
    public Object exportAnnotationsHandler(HttpServletRequest request, HttpServletResponse response) {
        return jsonOut(annotationHandler.exportAnnotationsHandler(request, response));
    }

    /**
     * Download document as PDF file [POST request]
     *
     * @param request HTTP servlet request
     * @return
     */
    @Override
    @RequestMapping(value = GET_PDF_VERSION_OF_DOCUMENT_HANDLER, method = RequestMethod.POST)
    public Object getPdfVersionOfDocumentHandler(HttpServletRequest request, HttpServletResponse response) {
        return jsonOut(annotationHandler.getPdfVersionOfDocumentHandler(request, response));
    }

    /**
     * Request to create annotation on document [POST request]
     *
     * @param request HTTP servlet request
     * @return
     */
    @Override
    @RequestMapping(value = CREATE_ANNOTATION_HANDLER, method = RequestMethod.POST)
    public Object createAnnotationHandler(HttpServletRequest request, HttpServletResponse response) {
        return jsonOut(annotationHandler.createAnnotationHandler(request, response));
    }

    /**
     * Get avatar for current user [GET request]
     *
     * @param request HTTP servlet request
     * @param response
     * @param userId  user id
     * @return
     */
    @RequestMapping(value = GET_AVATAR_HANDLER, method = RequestMethod.GET)
    public Object getAvatarHandler(HttpServletRequest request, HttpServletResponse response, String userId) {
        return annotationHandler.getAvatarHandler(request, response, userId);
    }

    /**
     * Add reply to annotation [POST request]
     *
     * @param request HTTP servlet request
     * @return
     */
    @Override
    @RequestMapping(value = ADD_ANNOTATION_REPLY_HANDLER, method = RequestMethod.POST)
    public Object addAnnotationReplyHandler(HttpServletRequest request, HttpServletResponse response) {
        return jsonOut(annotationHandler.addAnnotationReplyHandler(request, response));
    }

    /**
     * Edit reply for annotation [POST request]
     *
     * @param request HTTP servlet request
     * @return
     */
    @Override
    @RequestMapping(value = EDIT_ANNOTATION_REPLY_HANDLER, method = RequestMethod.POST)
    public Object editAnnotationReplyHandler(HttpServletRequest request, HttpServletResponse response) {
        return jsonOut(annotationHandler.editAnnotationReplyHandler(request, response));
    }

    /**
     * Delete reply from annotation [POST request]
     *
     * @param request HTTP servlet request
     * @return
     */
    @Override
    @RequestMapping(value = DELETE_ANNOTATION_REPLY_HANDLER, method = RequestMethod.POST)
    public Object deleteAnnotationReplyHandler(HttpServletRequest request, HttpServletResponse response) {
        return jsonOut(annotationHandler.deleteAnnotationReplyHandler(request, response));
    }

    /**
     * Delete annotation [POST request]
     *
     * @param request HTTP servlet request
     * @return
     */
    @Override
    @RequestMapping(value = DELETE_ANNOTATION_HANDLER, method = RequestMethod.POST)
    public Object deleteAnnotationHandler(HttpServletRequest request, HttpServletResponse response) {
        return jsonOut(annotationHandler.deleteAnnotationHandler(request, response));
    }

    /**
     * Save text field annotation [POST request]
     *
     * @param request HTTP servlet request
     * @return
     */
    @Override
    @RequestMapping(value = SAVE_TEXT_FIELD_HANDLER, method = RequestMethod.POST)
    public Object saveTextFieldHandler(HttpServletRequest request, HttpServletResponse response) {
        return jsonOut(annotationHandler.saveTextFieldHandler(request,response));
    }

    /**
     * Set color for text field annotation [POST request]
     *
     * @param request HTTP servlet request
     * @return
     */
    @Override
    @RequestMapping(value = SET_TEXT_FIELD_COLOR_HANDLER, method = RequestMethod.POST)
    public Object setTextFieldColorHandler(HttpServletRequest request, HttpServletResponse response) {
        return jsonOut(annotationHandler.setTextFieldColorHandler(request, response));
    }

    /**
     * Set annotation marker position [POST request]
     *
     * @param request HTTP servlet request
     * @return
     */
    @Override
    @RequestMapping(value = MOVE_ANNOTATION_MARKER_HANDLER, method = RequestMethod.POST)
    public Object moveAnnotationMarkerHandler(HttpServletRequest request, HttpServletResponse response) {
        return jsonOut(annotationHandler.moveAnnotationMarkerHandler(request, response));
    }

    /**
     * Set new size for annotation [POST request]
     *
     * @param request HTTP servlet request
     * @return
     */
    @RequestMapping(value = RESIZE_ANNOTATION_HANDLER, method = RequestMethod.POST)
    @Override
    public Object resizeAnnotationHandler(HttpServletRequest request, HttpServletResponse response) {
        return jsonOut(annotationHandler.resizeAnnotationHandler(request, response));
    }

    @RequestMapping(value = GET_DOCUMENT_COLLABORATORS_HANDLER, method = RequestMethod.POST)
    @Override
    public Object getDocumentCollaboratorsHandler(HttpServletRequest request, HttpServletResponse response) {
        return jsonOut(annotationHandler.getDocumentCollaboratorsHandler(request, response));
    }

    protected static ResponseEntity<String> jsonOut(Object obj) {
        return typeOut(obj, MediaType.APPLICATION_JSON);
    }

    protected static ResponseEntity<String> typeOut(Object obj, MediaType mediaType) {
        HttpHeaders httpHeaders = new HttpHeaders();
        if (mediaType == MediaType.APPLICATION_JSON) {
            httpHeaders.set("Content-type", "application/json;charset=UTF-8");
        } else {
            httpHeaders.setContentType(mediaType);
        }
        return new ResponseEntity<String>(obj.toString(), httpHeaders, HttpStatus.CREATED);
    }

    public int getIntFromColor(Color color){
        return getIntFromColor(color.getRed(), color.getGreen(), color.getBlue());
    }

    public int getIntFromColor(float red, float green, float blue){
        int R = Math.round(255 * red);
        int G = Math.round(255 * green);
        int B = Math.round(255 * blue);

        R = (R << 16) & 0x00FF0000;
        G = (G << 8) & 0x0000FF00;
        B = B & 0x000000FF;

        return 0xFF000000 | R | G | B;
    }
}
