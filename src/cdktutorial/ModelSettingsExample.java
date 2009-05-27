package cdktutorial;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.Renderer;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.RenderingParameters.AtomShape;
import org.openscience.cdk.renderer.font.AWTFontManager;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator;
import org.openscience.cdk.renderer.generators.BasicBondGenerator;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.visitor.AWTDrawVisitor;
import org.openscience.cdk.templates.MoleculeFactory;

public class ModelSettingsExample {
    
    public static void main(String[] args) throws IOException {
        Renderer renderer = new Renderer(
                ModelSettingsExample.getGenerators(), new AWTFontManager());
        
        // NOTE : it is essential to use a model from the renderer!
        RendererModel model = renderer.getRenderer2DModel();
        model.setKekuleStructure(true);
        model.setIsCompact(true);
        model.setCompactShape(AtomShape.OVAL);
        model.setBondWidth(4);
        model.setAtomRadius(9);
        model.setZoomFactor(1.2);
        
        ModelSettingsExample.draw(
                MoleculeFactory.makeSteran(), renderer);
    }
    
    public static List<IGenerator> getGenerators() {
        List<IGenerator> generators = new ArrayList<IGenerator>();
        generators.add(new BasicBondGenerator());
        generators.add(new BasicAtomGenerator());
        return generators;
    }
    
    // the same code as in the SingleMoleculeExample class
    public static void draw(IMolecule molecule, Renderer renderer) throws IOException {
        int WIDTH = 300;
        int HEIGHT = 300;
        
        Rectangle drawArea = new Rectangle(WIDTH, HEIGHT);
        Image image = new BufferedImage(
                WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        
        StructureDiagramGenerator sdg = new StructureDiagramGenerator();
        sdg.setMolecule(molecule);
        try {
            sdg.generateCoordinates();
        } catch (Exception e) { }
        molecule = sdg.getMolecule();
        
        renderer.setup(molecule, drawArea);
        
        // paint the background
        Graphics2D g2 = (Graphics2D)image.getGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, WIDTH, HEIGHT);
        
        renderer.paintMolecule(molecule, new AWTDrawVisitor(g2));
        
        ImageIO.write((RenderedImage)image, "PNG", new File("ball_stick.png"));
    }
    

}
