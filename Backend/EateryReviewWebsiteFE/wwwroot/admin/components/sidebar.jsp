<%-- 
    Document   : sidebar
    Created on : Jun 3, 2024, 11:45:45 AM
    Author     : legion
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!-- Sidebar  -->
<nav id="sidebar">
    <div class="sidebar_blog_1">
        <div class="sidebar-header">
            <div class="logo_section">
                <a href="index.html"><img class="logo_icon img-responsive" src="images/logo/logo_icon.png" alt="#" /></a>
            </div>
        </div>
        <div class="sidebar_user_info">
            <div class="icon_setting"></div>
            <div class="user_profle_side">
                <div class="user_img"><img src="data:image/*;base64,${sessionScope.currentUser.profilePicture}" style="width:  70px; height: 70px" alt="Not Available"></div>
                <div class="user_info">
                    <h6>${sessionScope.currentUser.displayName}</h6>
                    <p><span class="online_animation"></span> Online</p>
                </div>
            </div>
        </div>
    </div>

    <div class="sidebar_blog_2">
        <h4>General</h4>
        <ul class="list-unstyled components">
            <li class="active">
                <a href="dashboard" ><i class="fa fa-dashboard yellow_color"></i> <span>Dashboard</span></a>

            </li>

            <li><a href="show-mod"><i class="fa fa-group green_color"></i> <span>Moderators</span></a></li>
        </ul>
    </div>

</nav>
<!-- end sidebar -->
