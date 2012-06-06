<!DOCTYPE html>
<html lang="en">
<head>
    <meta content="app" name="layout"/>
    <title><g:message code="app.title"/></title>
    <r:require modules="app"/>
</head>

<body>


<div class="container-fluid">
    <div class="row-fluid">
        <div class="span3">
            <h5>LISTS</h5>
            <ul class="unstyled" id="lists" style="position: relative;">
                <div id="system-lists">

                    <li class="list ui-droppable selected" id="list-54000"><a data-list-id="54000" data-list="inbox" class="list-link type-inbox" href="#!/lists/inbox"><span></span> Inbox</a></li><li class="list ui-droppable" id="list-54001"><a data-list-id="54001" data-list="read-later" class="list-link type-read-later" href="#!/lists/read-later"><span></span> Read Later</a></li></div>
                <li class="list list-starred"><a data-list="starred" class="list-link type-starred" href="#/lists/starred/">Starred</a></li>
                <li class="list list-all"><a data-list="all" class="list-link type-all" href="#/lists/all">All Clips</a></li>
                <div id="user-lists" class="ui-sortable">

                </div>
                <li id="new_list">
                    <a class="add-list" href="">+ New list</a>
                    <form class="list-input" action=".">
                        + <input type="text" class="list-name" value="">
                    </form>
                </li>
            </ul>
        </div>

        <div class="span9">
            <div id="clips">
                <div class="list-meta clearfix">
                    <h3 class="name">Inbox</h3>
                    <ul class="list-actions unstyled">
                        <li><a title="List RSS feed (Private)" class="rss" href="/feed/kpelykh/ggT43otoaZufOWRyoFFYTi/inbox">RSS</a></li>
                        <li class="user-list" style="display: none;"><a class="edit" href="#">Edit</a></li>
                        <li class="user-list" style="display: none;"><a class="delete" href="#">Delete</a></li>
                    </ul>
                </div>
                <div id="create-clip">
                    <input type="text" placeholder="http://" id="new-clip">
                    <div class="clip-loading"><span>Loading...</span></div>
                </div>

                <div id="clips-container">
                    <ul class="unstyled" id="clip-list" style="display: block;"></ul>
                    <div id="no-clips" style="display: block;">
                        <strong>This list is empty</strong>
                        Add clips or <a href="/tools/">check out the tools</a>
                    </div>
                    <div id="no-search-results" style="display: none;">No search results.</div>
                </div>
                <div class="pagination">

                </div>
            </div>
            <div id="notes" style="display: none;"></div>
        </div>
    </div>

</div>


</body>
</html>