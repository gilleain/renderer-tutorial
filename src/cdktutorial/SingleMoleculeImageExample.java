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
import org.openscience.cdk.renderer.font.AWTFontManager;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator;
import org.openscience.cdk.renderer.generators.BasicBondGenerator;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.visitor.AWTDrawVisitor;
import org.openscience.cdk.templates.MoleculeFactory;

/**
 * Simple example of drawing a single molecule onto an image.
 * 
 * @author maclean
 *
 */
public class SingleMoleculeImageExample {
    
    public static void main(String[] args) throws IOException {
        int WIDTH = 200;
        int HEIGHT = 200;
        
        // the draw area and the image should be the same size
        Rectangle drawArea = new Rectangle(WIDTH, HEIGHT);
        Image image = new BufferedImage(
                WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        
        // any molecule will do
        IMolecule triazole = MoleculeFactory.make123Triazole();
        StructureDiagramGenerator sdg = new StructureDiagramGenerator();
        sdg.setMolecule(triazole);
        try {
            sdg.generateCoordinates();
        } catch (Exception e) { }
        triazole = sdg.getMolecule();
        
        
        // generators make the image elements
        List<IGenerator> generators = new ArrayList<IGenerator>();
        generators.add(new BasicBondGenerator());
        generators.add(new BasicAtomGenerator());
        
        // the renderer needs to have a toolkit-specific font manager 
        Renderer renderer = new Renderer(generators, new AWTFontManager());
        
        // the call to 'setup' only needs to be done on the first paint
        renderer.setup(triazole, drawArea);
        
        // paint the background
        Graphics2D g2 = (Graphics2D)image.getGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, WIDTH, HEIGHT);
        
        // the paint method also needs a toolkit-specific renderer
        renderer.paintMolecule(triazole, new AWTDrawVisitor(g2));
        
        ImageIO.write((RenderedImage)image, "PNG", new File("triazole.png"));
    }

}
