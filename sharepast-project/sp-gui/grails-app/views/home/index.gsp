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
        <h1>Great way to share your past events with your friends</h1>

        <p class="intro">
            Sharepast makes it easy to save, search, share
            photos and and video of your past.
            .
        </p>

        <p><a class="btn btn-green btn-large" href="signup.html">Sign up, it's free</a></p>
    </header>

    <section class="row features">
        <h2>Find greatest events of your past that you miss so much</h2>

        <p class="intro">Create, upload and share with your friends.</p>

    </section>

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

</article>

</body>
</html>