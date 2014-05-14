<div name="summaryTab" class="comments_content ui-tabs-panel ui-widget-content ui-corner-bottom" style="height: 100%;margin-top:0">
    <div class="comments_scroll_2" style="height: 473px;">
        <div class="viewport" style="height: 453px;">
            <div class="overview" style="top: 0px;">
                <h3 class="com_heading">Summary:</h3>
                <div data-bind="foreach: annotationsByPages">
                    <!-- ko if: (typeof $data != "undefined" && $data.length > 0) -->
                        <h3 class="page_number_in_summary" data-bind="text: 'Page ' + ($index() + 1)"></h3>
                        <!-- ko foreach: $data -->
                            <div class="collapsed_anno_box" data-bind="click: function(){$root.activateAnnotation($data);}">
                                <a class="anno_select anno_universal_icon" href="#">
                                <span class="comments_number" 
                                    data-bind="visible: $data.replyCount, text: $data.replyCount, style: {backgroundColor: $root.getLastReplyColor($data)}"></span>
                                </a>
                                <a href="#" class="tab_sum_comment_text" data-bind="text: $root.getLastReplyText($data)"></a>
                            </div>
                        <!-- /ko -->
                    <!-- /ko -->
                </div>
            </div>
        </div>
    </div>
</div>