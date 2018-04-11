package controllers;

import play.db.jpa.Transactional;
import play.mvc.Http;
import play.mvc.Result;
import services.AdministratorService;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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


    public Result fileUpload() throws IOException {
        Http.MultipartFormData<File> body = request().body().asMultipartFormData();
        String restaurantId = body.asFormUrlEncoded().get("restaurantId")[0];
        String imageType = body.asFormUrlEncoded().get("imageType")[0];
        String savePath = "public/assets/images/";
        Http.MultipartFormData.FilePart<File> picture = body.getFile("file");
        if (picture != null) {
            String fileName = picture.getFilename();
            String contentType = picture.getContentType();
            File file = picture.getFile();
            String thumbnailPath = savePath + restaurantId + "thumb" + fileName;
            //saveScaledImage(file.getPath(),thumbnailPath);
            String imagePath;
            if (imageType.equals("profile") || imageType.equals("cover")) {
                imagePath = savePath + restaurantId + "-" + imageType + ".jpg";

            } else {
                imagePath = savePath + restaurantId + "-" + imageType + ".jpg" ;
            }
            Path temp = Files.move(file.toPath(), Paths.get(imagePath));
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

    private void saveScaledImage(String filePath, String outputFile) {
        try {

            BufferedImage sourceImage = ImageIO.read(new File(filePath));
            int width = sourceImage.getWidth();
            int height = sourceImage.getHeight();

            if (width > height) {
                float extraSize = height - 200;
                float percentHight = (extraSize / height) * 100;
                float percentWidth = width - ((width / 100) * percentHight);
                BufferedImage img = new BufferedImage((int) percentWidth, 200, BufferedImage.TYPE_INT_RGB);
                Image scaledImage = sourceImage.getScaledInstance((int) percentWidth, 200, Image.SCALE_SMOOTH);
                img.createGraphics().drawImage(scaledImage, 0, 0, null);
                BufferedImage img2 = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
                img2 = img.getSubimage((int) ((percentWidth - 100) / 2), 0, 200, 200);

                ImageIO.write(img2, "jpg", new File(outputFile));
            } else {
                float extraSize = width - 200;
                float percentWidth = (extraSize / width) * 100;
                float percentHight = height - ((height / 100) * percentWidth);
                BufferedImage img = new BufferedImage(200, (int) percentHight, BufferedImage.TYPE_INT_RGB);
                Image scaledImage = sourceImage.getScaledInstance(200, (int) percentHight, Image.SCALE_SMOOTH);
                img.createGraphics().drawImage(scaledImage, 0, 0, null);
                BufferedImage img2 = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
                img2 = img.getSubimage(0, (int) ((percentHight - 100) / 2), 200, 200);

                ImageIO.write(img2, "jpg", new File(outputFile));
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    @Transactional
    public Result getStatistics() {
        return wrapForAdmin(() -> this.service.getStatistics());
    }
}
