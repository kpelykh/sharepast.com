<#import "libs/util.ftl" as util>

<#assign link in layout>
<link href="/css/home.css" rel="stylesheet"/>
</#assign>


<@layout.defaultlayout >

<section id="content">
    <section id="know-fedena" class="content-area">
        <h3>Get Involved</h3>

        <p>
            Test paragraph
        </p>

        <p><a href="/pages/about" class="alignright">More details</a></p>

    </section>


    <section id="fedena-code-war" class="recent-forum-topics">

        <h3>Recent Forum posts</h3>


        <p>
            <a href="/forum/2-fedena-installation-and-troubleshooting/topics/228-heroku-rake-db-migrate-error-while-deploy-to-heroku?page=1#946">vlpeng</a></b>
            :<br/>
            <a href="/forum/2-fedena-installation-and-troubleshooting/topics/228-heroku-rake-db-migrate-error-while-deploy-to-heroku?page=1#946"
               class="recent-forum-topics-link">Hi there, Is there any people encounter below issue while deploy to
                Heroku...</a>
        </p>


        <p><a href="/recent_posts" class="alignright">More Forum topics</a></p>
    </section>

    <nav id="side-nav">

        <ul>
            <li class="top-contributors"><a href="/pages/contributors">Top Contributors</a></li>
            <li class="documentation"><a href="/changelog">Release Notes</a></li>
            <li class="how-to-contribute"><a href="/pages/contribute">How to contribute</a></li>
            <li class="download-center"><a href="/pages/download">Download Center</a></li>
            <li class="architecture"><a href="/pages/architecture">Architecture</a></li>

        </ul>
    </nav>

</section>
</@layout.defaultlayout>

