<div name="replyTab" class="comments_content ui-tabs-panel ui-widget-content ui-corner-bottom" style="height: 100%;margin-top:0">
    <div class="comments_scroll" style="height: 100%;">
        <div class="viewport" style="height: 433px;">
            <div id="repliesTab" class="overview" style="top: 0px;">
                <a class="red_button_sb right" href="#" data-bind="click: function() { $root.deleteActiveAnnotation(); }, visible:$root.activeAnnotation() != null">Delete Annotation</a>
                <h3 class="com_heading">Comments for annotation:</h3>
                 <!-- ko if: activeAnnotation() != null -->
                     <!-- ko foreach: activeAnnotation().replies -->
                         <div class="comment_box_sidebar" data-bind="css: {'lvl1': replyLevel == 0, 'lvl2': replyLevel == 1, 'lvl3':replyLevel >= 2}">
                            <div class="comment_avatar"><span class="blanc_avatar_icon"></span>
                            <img alt="" style="position: absolute; left: 0;" data-bind="visible: avatarUrl, src: avatarUrl" onerror="$(this).hide();" onload="if (this.width == 0) { $(this).hide(); }" /></div>
                            <span class="comment_name" data-bind="text: userName, visible: guid() != null"></span>
                            <p class="comment_time" data-bind="text: displayDateTime, visible: guid() != null"></p>
                            <div class="comment_text_wrapper">
                                <span class="comment_box_pointer"></span>
                                <div class="comment_text" data-bind="htmlValue: text, 
                                                                 attr: {contenteditable: $root.activeAnnotation().activeReply() == $index() || $data.guid() == null},
                                                                 hasfocus: $root.activeAnnotation().activeReply() == $index(),
                                                                 event: {blur: function(){$root.commitReplyOfActiveAnnotation($index());} }">
                                </div>
                            </div>
                            <!-- ko if: guid() != null -->
                                <!-- ko if: $data.userGuid == $root.userGuid -->
                                    <a class="comment_reply_btn" data-bind="visible: $root.activeAnnotation().activeReply() != $index(), click: function(){ $root.activeAnnotation().activeReply($index()) }" href="#">Edit</a>
                                    <a class="comment_reply_btn" data-bind="visible: $root.activeAnnotation().activeReply() != $index(), click: 
                                        function(){ $root.deleteReply($root.activeAnnotation().guid, $data.guid()); }" href="#">Delete</a>
                                <!-- /ko -->
                            <!-- ko if: $data.userGuid != $root.userGuid -->
                                <a class="comment_reply_btn" data-bind="visible: $root.activeAnnotation().activeReply() != $index(), click: function(){ $root.addChildReply($root.activeAnnotation(), $data.guid()) }" href="#">Reply</a>
                            <!-- /ko -->
                            <a class="red_button_sb right" href="#" 
                               data-bind="visible: $root.activeAnnotation().activeReply() == $index(), 
                                          click: function(data, e) { $root.commitReplyOfActiveAnnotation($index()); }, clickBubble: false">Submit</a>
                            <a class="comment_reply_btn" href="#" data-bind="visible: $root.activeAnnotation().activeReply() == $index(), click: function(data, e) { $root.activeAnnotation().activeReply(-1); }">Cancel</a>
                            <!-- /ko -->
                            <!-- ko if: guid() == null -->
                                <a class="red_button_sb right" href="#" 
                                   data-bind="visible: $root.activeAnnotation().activeReply() == $index(), 
                                              click: function(data, e) { $root.commitReplyOfActiveAnnotation($index()); return false;}, clickBubble: false">Submit</a>
                                <a class="comment_reply_btn" href="#" data-bind="click: $root.deleteReply.bind($root, $data)">Cancel</a>
                            <!-- /ko -->
                        </div>
                    <!-- /ko -->
                <!-- /ko -->
            </div>
        </div>
    </div>
</div>