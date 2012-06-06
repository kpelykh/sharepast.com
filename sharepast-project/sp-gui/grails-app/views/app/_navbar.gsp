<g:render template="../content/logos"/>

<div class="btn-group pull-right">
    <a class="btn dropdown-toggle" data-toggle="dropdown" href="#">
        <i class="icon-user"></i> <sec:principal property="username"/>
        <span class="caret"></span>
    </a>
    <ul class="dropdown-menu">
        <li><a href="/user/settings">Settings</a></li>
        <li class="divider"></li>
        <li><a href="/user/logout">Sign Out</a></li>
    </ul>
</div>

<div class="nav-collapse">
    <ul class="nav">
        <li class="active"><a href="#">Home</a></li>
        <li><a href="#about">About</a></li>
        <li><a href="#contact">Contact</a></li>
    </ul>
</div><!--/.nav-collapse -->
