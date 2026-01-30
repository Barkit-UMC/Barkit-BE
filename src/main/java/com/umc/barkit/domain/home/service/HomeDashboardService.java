package com.umc.barkit.domain.home.service;

import com.umc.barkit.domain.home.dto.response.HomeDashboardResponse;

public interface HomeDashboardService {

    HomeDashboardResponse.DashboardDTO getDashboard();
}
