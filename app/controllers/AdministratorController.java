package controllers;

import play.db.jpa.Transactional;
import services.AdministratorService;
import play.mvc.Result;

import javax.inject.Inject;

/**
 * The type Administrator controller.
 */
public class AdministratorController extends BaseController {

    private AdministratorService service;

    /**
     * Sets service.
     *
     * @param service the service
     */
    @Inject
    public void setService(final AdministratorService service) {
        this.service = service;
    }

    @Transactional
    public Result getStatistics() {
        return wrapForAdmin(() -> this.service.getStatistics());
    }
}
