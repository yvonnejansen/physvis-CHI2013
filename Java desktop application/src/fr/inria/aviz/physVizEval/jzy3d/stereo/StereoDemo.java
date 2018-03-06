package fr.inria.aviz.physVizEval.jzy3d.stereo;

import java.awt.Dimension;
import java.awt.Rectangle;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartView;
import org.jzy3d.chart.controllers.mouse.ChartMouseController;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.factories.CameraFactory;
import org.jzy3d.factories.JzyFactories;
import org.jzy3d.factories.ViewFactory;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Builder;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.legends.colorbars.ColorbarLegend;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.modes.CameraMode;
import org.jzy3d.ui.ChartLauncher;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;

public class StereoDemo {
    protected static boolean ENABLE_STEREO = true;
    protected static boolean ENABLE_PERSPECTIVE = true;
    protected static boolean ENABLE_COLORBAR = false;

    public static void main(String[] args) throws Exception {
        // Define a function to plot
        Mapper mapper = new Mapper() {
            public double f(double x, double y) {
                return 10 * Math.sin(x / 10) * Math.cos(y / 20) / x;
            }
        };

        // Define range and precision for the function to plot
        Range range = new Range(-150, 150);
        int steps = 50;

        // Create the object to represent the function over the given range.
        final Shape surface = (Shape) Builder.buildOrthonormal(new OrthonormalGrid(range, steps, range, steps), mapper);
        surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(), surface.getBounds().getZmax(), new Color(1, 1, 1, .5f)));
        surface.setFaceDisplayed(true);
        surface.setWireframeDisplayed(true);
        surface.setWireframeColor(Color.BLACK);

        // ------------------------------
        // Create a chart and add surface
        Chart chart;
        if (ENABLE_STEREO){
            JzyFactories.view = new ViewFactory() {
                public ChartView getInstance(Scene scene, ICanvas canvas, Quality quality) {
                    return new StereoView(scene, canvas, quality);
                }
            };
            JzyFactories.camera = new CameraFactory(){
                public Camera getInstance(Coord3d center) {
                    return new StereoCamera(center);
                }
            };
        
        GLCapabilities cap = new GLCapabilities(GLProfile.getDefault());
        cap.setStereo(true);
        cap.setDoubleBuffered(true);
        chart = new Chart(Quality.Nicest, "awt", cap);
        chart.getScene().getGraph().add(surface);
        chart.addController(new ChartMouseController());
        }
        else {
            chart = new Chart(Quality.Nicest);
            chart.getScene().getGraph().add(surface);

        }

        if (ENABLE_PERSPECTIVE) {
            chart.getView().setCameraMode(CameraMode.PERSPECTIVE);
            // in perspective, axe box text has some problems because
            // part of the box it out of the screen.
            // to be solved
//            chart.getView().setAxeBoxDisplayed(false);
        }

        // -----------------
        // Setup a colorbar
        if (ENABLE_COLORBAR) {
            ColorbarLegend cbar = new ColorbarLegend(surface, chart.getView().getAxe().getLayout());
            cbar.setMinimumSize(new Dimension(100, 600));
            surface.setLegend(cbar);
        }
        
        // -----------------
        // Open in a window
        ChartLauncher.instructions();
        ChartLauncher.openChart(chart, new Rectangle(0, 0, 1200, 1000), "Stereo Demo");
        ChartLauncher.screenshot(chart, "./data/screenshots/stereo.png");
        System.out.println("near " + chart.getView().getCamera().getNear() + " far " + chart.getView().getCamera().getFar());
    }

}
