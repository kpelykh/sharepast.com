<!DOCTYPE html>
<html lang="en">
<head>
    <meta content="home" name="layout"/>
    <title><g:message code="home.title"/></title>
    <r:require modules="app"/>
    <parameter name="activeTab" value="index" />
</head>

<body>

<article class="container">

    <header class="hero-unit">
        <h1>Great way to clip and read the web</h1>

        <p class="intro">
            Kippt makes it easy to save, search
            and read the information you find on the web.
        </p>

        <p><a class="btn btn-green btn-large" href="signup.html">Sign up, it's free</a></p>
    </header>

    <section class="row features">
        <h2>Manage your bookmarks and reading beautifully</h2>

        <p class="intro">Lightweight, fast and with features you actually use.</p>

        <div class="span6">
            <i class="bookmarking"></i>

            <h3>Simpler and faster bookmarking</h3>

            <p>
                Save and organize your links in to simple lists
                with notes and tags. Private or public.</p>
        </div>

        <div class="span6">
            <i class="search"></i>

            <h3>Search everything</h3>

            <p>In Kippt, everything you collect is searchable from the title to the page content.
        </div>

        <div class="span6">
            <i class="read"></i>

            <h3>Read your articles later</h3>

            <p>Timeshift your reading for a better time. Read
            the articles easily on desktop and mobile.</p>
        </div>

        <div class="span6">
            <i class="collaborate"></i>

            <h3>Collaborate and share <span class="label label-warning">Coming soon</span></h3>

            <p>Collect bookmarks and clips with your collegues. Curate and share the best you find on the web.</p>
        </div>

        <g:render template="/common/follow-buttons"/>

    </section>

    <hr>

    <r:script>
        $(function () {
            var tabContainers = $('section.screenshots .tab');
            tabContainers.hide().filter(':first').show();

            $('section.screenshots ul.tabs a').click(function () {
                tabContainers.hide();
                tabContainers.filter(this.hash).fadeIn(400);
                $('section.screenshots ul.tabs a').removeClass('selected');
                $(this).addClass('selected');
                return false;
            }).filter(':first').click();
        });

    </r:script>


    <section class="row screenshots">

        <div>
            <div id="first" class="tab">
                <h4>Keep it simple with lists</h4>
                <img src="https://kippt.com/static/img/home-screen-overview.png" alt="">
            </div>

            <div id="second" class="tab">
                <h4>Read interesting articles later. Also with mobile.</h4>
                <img src="https://kippt.com/static/img/home-screen-readlater.png" alt="">
            </div>

            <div id="third" class="tab">
                <h4>Search from page content, title and urls. Forget tedious tagging.</h4>
                <img src="https://kippt.com/static/img/home-screen-search.png" alt="">
            </div>

            <div id="fourth" class="tab">
                <h4>Import your bookmarks to Kippt.</h4>
                <img src="https://kippt.com/static/img/home-screen-import.png" alt="">
            </div>

            <div class="shadow"></div>
        </div>

        <ul class="tabs">
            <li><a href="#first">Overview</a></li>
            <li><a href="#second">Read<span>Later</span></a></li>
            <li><a href="#third">Search</a></li>
            <li><a href="#fourth">Import</a></li>
        </ul>

    </section>

    <hr>

    <section class="row testimonials">
        <div class="span4">
            <blockquote>
                <span>Kippt: a bookmarking app to watch</span>
                <a href="http://www.readwriteweb.com/archives/kippt_a_bookmarking_app_to_watch.php"><img
                        src="https://kippt.com/static/img/readwriteweb.png" alt="ReadWriteWeb"></a>
            </blockquote>

        </div>

        <div class="span4">
            <blockquote>
                <span>Kippt offers easy bookmarking, sharing and reading tools in one beautiful free app.</span>
                <a class="appstorm"
                   href="http://web.appstorm.net/reviews/data-management/get-the-most-out-of-your-bookmarks-with-kippt/#more-26916">Appstorm</a>
            </blockquote>
        </div>

        <div class="span4">
            <blockquote>
                <span>Loving @kippt bookmark organizer! Simple, beautiful and easy to use. What else is needed?</span>
                <a href="https://twitter.com/#!/nikosalonen/status/173766465781104640">@nikosalonen</a>
            </blockquote>
        </div>
    </section>

    <hr/>

    <section class="row beawesome">
        <h2>Be organized. Use Kippt.</h2>
        <a href="signup.html" class="btn btn-green btn-large">Sign up, it's free</a>
    <section>

</article>

</body>
</html>