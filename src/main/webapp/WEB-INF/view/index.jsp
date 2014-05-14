<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html>
    <head>
        <meta charset="utf-8">
        <title>GroupDocs.Annotation for Java Sample</title>
        ${groupdocsHeader}
    </head> 
    <body>
        <style type="text/css">
            .ControlLink{cursor:pointer; display:inline-block; text-decoration: underline; margin-right: 5px}
            .NumberInput{width:50px; margin-right: 5px}
            .grpdx .collapsed_anno_box .tab_sum_comment_text {
                color: #393939;
             }
        </style>

        <div style="float:left;width:100%">
            <a id="firstPageButton" class="ControlLink">First Page</a>
            <a id="prevPageButton" class="ControlLink">Previous Page</a>
            <input type="text" id="setPageInput" class="NumberInput"/>
            <a id="nextPageButton" class="ControlLink">Next Page</a>
            <a id="lastPageButton" class="ControlLink">Last Page</a>
            <a id="showFileBrowserButton" class="ControlLink">Show File Browser</a>

            <a id="zoomInButton" class="ControlLink">Zoom In</a>
            <a id="zoomOutButton" class="ControlLink">Zoom Out</a>
            <input type="text" id="setZoomInput" class="NumberInput"/>
        </div>

        <!--<div id="annotation-widget" style="width:${width}px;height:${height}px;overflow:hidden;position:relative;margin-bottom:20px;background-color:gray;border:1px solid #ccc;"></div>-->
        <div id="annotation-widget" style="width:${width}px;height:100%;float:left"></div>
        ${groupdocsScripts}

        <div class="grpdx" style="width: 200px; height: 100%; float:left">
            <jsp:include page="summaryTab.jsp"></jsp:include>
        </div>
        <div class="grpdx" style="height: 100%; float:left">
            <jsp:include page="replyTab.jsp"></jsp:include>
        </div>

        <script type="text/javascript">
            $(function () {
                var containerElement = $("#annotation-widget");

                //window.onresize = resize;
                //function resize() {
                //    var width = containerElement.width();
                //    containerElement.groupdocsAnnotation("setWidth", width);
                //    var height = containerElement.height();
                //    containerElement.groupdocsAnnotation("setHeight", height);
                //}

                $('#firstPageButton').click(function () {
                    containerElement.groupdocsAnnotation("openFirstPage");
                });

                $('#lastPageButton').click(function () {
                    containerElement.groupdocsAnnotation("openLastPage");
                });

                $('#prevPageButton').click(function () {
                    containerElement.groupdocsAnnotation("openPreviousPage");
                });

                $('#setPageInput').bind("keypress", function (e) {
                    var code = e.keyCode || e.which;
                    if (code == 13) { //Enter keycode
                        containerElement.groupdocsAnnotation("setPage", Number($(this).val()));
                    }
                });


                containerElement.groupdocsAnnotation("on", "documentPageSet.groupdocs", function (e, pageNumber) {
                    console.log("documentPageSet.groupdocs");
                    $("#setPageInput").val(pageNumber);
                });

                containerElement.groupdocsAnnotation("on", "documentScrolledToPage.groupdocs", function (e, pageNumber) {
                    console.log("documentScrolledToPage.groupdocs");
                    $("#setPageInput").val(pageNumber);
                });


                $('#nextPageButton').click(function () {
                    containerElement.groupdocsAnnotation("openNextPage");
                });

                $('#showFileBrowserButton').click(function () {
                    containerElement.groupdocsAnnotation("showFileBrowser");
                });

                $('#zoomInButton').click(function () {
                    containerElement.groupdocsAnnotation("zoomIn");
                });

                $('#zoomOutButton').click(function () {
                    containerElement.groupdocsAnnotation("zoomOut", 1);
                });

                $('#setZoomInput').bind("keypress", function (e) {
                    var code = e.keyCode || e.which;
                    if (code == 13) { //Enter keycode
                        containerElement.groupdocsAnnotation("setZoom", Number($(this).val()));
                    }
                });

                containerElement.groupdocsAnnotation("on", "zoomSet.groupdocs", function (e, zoom) {
                    console.log("zoomSet.groupdocs");
                    $("#setZoomInput").val(zoom);
                });


                var summaryTabViewModel = {};
                summaryTabViewModel.annotationsByPages = containerElement.groupdocsAnnotation("getAnnotationsByPagesObservable");

                summaryTabViewModel.activateAnnotation = function (annotation) {
                    return containerElement.groupdocsAnnotation("activateAnnotation", annotation);
                };

                summaryTabViewModel.getLastReplyColor = function (annotation) {
                    return containerElement.groupdocsAnnotation("getLastReplyColor", annotation);
                };

                summaryTabViewModel.getLastReplyText = function (annotation) {
                    return containerElement.groupdocsAnnotation("getLastReplyText", annotation);
                };

                var summaryTabElement = $("[name='summaryTab']");
                ko.applyBindings(summaryTabViewModel, summaryTabElement[0]);


                var replyTabViewModel = {};
                replyTabViewModel.deleteActiveAnnotation = function () {
                    return containerElement.groupdocsAnnotation("deleteActiveAnnotation");
                };

                replyTabViewModel.activeAnnotation = containerElement.groupdocsAnnotation("getActiveAnnotationObservable");

                /*replyTabViewModel.askAndDeleteReply = function (annotationInput, replyInput) {
                    var annotation, reply;
                    annotation = annotationInput;
                    reply = replyInput;

                    var self = this;
                    function deleteAnnotationReply() { self.deleteReply(annotation.guid, reply.guid()); }

                    if (annotation.checkReplyHasChildren(reply.guid())) {
                        $('<div id="dialog" title="Delete">' +
                        'Are you sure you wish to delete this reply, as all children will also be deleted?' +
                        '</div>').dialog(
                            {
                                //modal: true,
                                buttons: {
                                    Yes: function () {
                                        deleteAnnotationReply();
                                        $(this).dialog("close");
                                    },
                                    No: function () {
                                        $(this).dialog("close");
                                    }
                                }
                            }
                        );

                        var ctx = {
                            title: 'Delete',
                            message: 'Are you sure you wish to delete this reply, as all children will also be deleted?',
                            action: deleteAnnotationReply
                        };
                        $('#confirmation-dialog').data('context', ctx).modal('show');
                    }
                    else {
                        deleteAnnotationReply();
                    }
                };*/

                replyTabViewModel.commitReplyOfActiveAnnotation = function (replyIndex) {
                    containerElement.groupdocsAnnotation("commitReplyOfActiveAnnotation", replyIndex);
                };

                replyTabViewModel.deleteReply = function (annotationGuid, replyGuid) {
                    containerElement.groupdocsAnnotation("deleteReply", annotationGuid, replyGuid);
                };

                replyTabViewModel.addChildReply = function (annotation, parentReplyGuid) {
                    return containerElement.groupdocsAnnotation("addChildReply", annotation, parentReplyGuid);
                };

                replyTabViewModel.userGuid = containerElement.groupdocsAnnotation("getCurrentUserGuid");

                var replyTabElement = $("[name='replyTab']");
                ko.applyBindings(replyTabViewModel, replyTabElement[0]);

            });
        </script>
    </body>
</html>
