<footer>
    <p>
        <a href="about.html">About us</a> &middot;
        <a href="mailto:hello@sharepast.com">Contact</a> &middot;
        <a href="http://blog.sharepast.com/" target="_blank">Blog</a>
    </p>
    <p>&copy; 2011-2012 SharePast. Share the best of your past.</p>

    <g:if test="${params.controller == 'home'}">
        <g:render template="/content/follow-buttons"/>
    </g:if>
    <g:else>
        <g:render template="/content/follow-buttons2"/>
    </g:else>

</footer>
