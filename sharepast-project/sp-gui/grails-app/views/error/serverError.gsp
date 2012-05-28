<html>
<head>
    <title><g:message code="error.500.title"/></title>
    <meta content="error" name="layout"/>
    <style type="text/css">
    .msg_list {
        margin: 0px;
        padding: 0px;
        width: 383px;
    }
    .msg_head {
        padding: 5px 10px;
        cursor: pointer;
        position: relative;
        background-color:#FFCCCC;
        margin:1px;
    }
    .msg_body {
        padding: 5px 10px 15px;
        background-color:#F4F4F8;
    }

    </style>
    <r:script>
        $(document).ready(function()
        {
        //hide the all of the element with class msg_body
            $(".msg_body").hide();
        //toggle the componenet with class msg_body
            $(".msg_head").click(function()
            {
            $(this).next(".msg_body").slideToggle(600);
            });
        });

    </r:script>
</head>
    <body>
        <h1>An Error has occurred</h1>
        <p>We're sorry, but there has been a problem rendering the page you've requested. This incident has been logged, and will be looked into soon.</p>

        <div class="msg_list">
            <p class="msg_head">Error Details</p>
            <div class="msg_body">
                ${exception.message}
            </div>
        </div>
    </body>
</html>