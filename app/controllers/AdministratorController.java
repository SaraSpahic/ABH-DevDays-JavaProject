package controllers;

import play.mvc.Http;
import play.db.jpa.Transactional;
import services.AdministratorService;
import play.mvc.Result;

import play.mvc.Result;
import javax.inject.Inject;
import java.io.File;

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

    public Result fileUpload() {
        Http.MultipartFormData<File> body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart<File> picture = body.getFile("file");
        if (picture != null) {
            String fileName = picture.getFilename();
            String contentType = picture.getContentType();
            File file = picture.getFile();
            return ok("File uploaded");
        } else {
            flash("error", "Missing file");
            return badRequest();
        }
    }

    public Result checkPreFlight() {
        response().setHeader("Access-Control-Allow-Origin", "*");       // Need to add the correct domain in here!!
        response().setHeader("Access-Control-Allow-Methods", "POST");   // Only allow POST
        response().setHeader("Access-Control-Max-Age", "300");          // Cache response for 5 minutes
        response().setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");         // Ensure this header is also allowed!
        return ok();
    }



    @Transactional
    public Result getStatistics() {
        return wrapForAdmin(() -> this.service.getStatistics());
    }
}
