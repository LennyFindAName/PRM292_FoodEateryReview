<%-- 
    Document   : topbar
    Created on : Jun 3, 2024, 12:00:43 PM
    Author     : legion
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!-- topbar -->
    <div class="topbar">
        <nav class="navbar navbar-expand-lg navbar-light">
            <div class="full">
                <button type="button" id="sidebarCollapse" class="sidebar_toggle"><i class="fa fa-bars"></i></button>
                <div class="logo_section">
<!--                    <a href="index.html"><img class="img-responsive" src="images/logo/logo.png" alt="#" /></a>-->
                </div>
                <div class="right_topbar">
                    <div class="icon_info">
<!--                        <ul>
                            <li><a href="#"><i class="fa fa-bell-o"></i><span class="badge">2</span></a></li>
                            <li><a href="#"><i class="fa fa-question-circle"></i></a></li>
                            <li><a href="#"><i class="fa fa-envelope-o"></i><span class="badge">3</span></a></li>
                        </ul>-->
                        <ul class="user_profile_dd">
                            <li>
                                <a class="dropdown-toggle" data-toggle="dropdown"><img src="data:image/*;base64,${sessionScope.currentUser.profilePicture}" style="width:  40px; height: 40px" alt="Not Available"><span class="name_user">${sessionScope.currentUser.displayName}</span></a>
                                <div class="dropdown-menu">
                                    <a class="dropdown-item" href="${pageContext.request.contextPath}/homepage">Homepage</a>
<!--                                    <a class="dropdown-item" href="profile.html">My Profile</a>
                                    <a class="dropdown-item" href="settings.html">Settings</a>
                                    <a class="dropdown-item" href="help.html">Help</a>-->
                                    <a class="dropdown-item" href="${pageContext.request.contextPath}/log-out"><span>Log Out</span> <i class="fa fa-sign-out"></i></a>
                                </div>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </nav>
    </div>
