<footer>
    <p>
        <a href="about.html">About us</a> &middot;
        <a href="mailto:hello@sharepast.com">Contact</a> &middot;
        <a href="http://blog.sharepast.com/" target="_blank">Blog</a>
    </p>
    <p>&copy; 2012 SharePast. Designed and developed with love in Sunnyvale, California &#12484; </p>

    <g:if test="${params.controller == 'home'}">
        <g:render template="/common/follow-buttons"/>
    </g:if>
    <g:else>
        <g:render template="/common/follow-buttons-no-g1"/>
    </g:else>

</footer>
