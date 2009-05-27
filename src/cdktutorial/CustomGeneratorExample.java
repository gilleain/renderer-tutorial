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
import javax.vecmath.Point2d;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.Renderer;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.OvalElement;
import org.openscience.cdk.renderer.font.AWTFontManager;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator;
import org.openscience.cdk.renderer.generators.BasicBondGenerator;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.visitor.AWTDrawVisitor;
import org.openscience.cdk.templates.MoleculeFactory;

/**
 * Example code for a custom generator class - the inner class 
 * 'BondMidpointGenerator' - that makes filled circles at the midpoint of the
 * bonds of a structure.
 * 
 * @author maclean
 *
 */
public class CustomGeneratorExample {
    
    public class BondMidpointGenerator implements IGenerator {

        public IRenderingElement generate(IAtomContainer ac, RendererModel model) {
            // this contains the rendering elements
            ElementGroup bondCircles = new ElementGroup();
            
            // most parameters are in screen space, and have to be scaled
            double r = model.getAtomRadius() / model.getScale();
            
            // make a circle at the midpoint of each bond
            for (IBond bond : ac.bonds()) {
                Point2d p = bond.get2DCenter();
                
                // the rendering element is a lightweight data class
                IRenderingElement oval = 
                    new OvalElement(p.x, p.y, r, true, Color.LIGHT_GRAY);
                
                // add to the group, so that they are all in the same layer
                bondCircles.add(oval);
            }
            return bondCircles;
        }
        
    }
    
    public List<IGenerator> getGenerators() {
        List<IGenerator> generators = new ArrayList<IGenerator>();
        generators.add(new BasicBondGenerator());
        generators.add(new BasicAtomGenerator());
        
        // we add our custom generator here, on the top
        generators.add(new BondMidpointGenerator());
        return generators;
    }
    
    // same as the makeImage method from the SingleMoleculeImage example
    public Image makeImage(IMolecule molecule, int w, int h) {
        Rectangle drawArea = new Rectangle(w, h);
        Image image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        
        Renderer renderer = new Renderer(getGenerators(), new AWTFontManager());
        renderer.setup(molecule, drawArea);
        renderer.getRenderer2DModel().setZoomFactor(2);
        renderer.getRenderer2DModel().setBondWidth(3);
        renderer.getRenderer2DModel().setAtomRadius(5);
        
        Graphics2D g2 = (Graphics2D)image.getGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, w, h);
        
        renderer.paintMolecule(molecule, new AWTDrawVisitor(g2));
        return image;
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws IOException {

        IMolecule mol = MoleculeFactory.make4x3CondensedRings();
        StructureDiagramGenerator sdg = new StructureDiagramGenerator();
        sdg.setMolecule(mol);
        try {
            sdg.generateCoordinates();
        } catch (Exception e) { }
        mol = sdg.getMolecule();
        
        Image image = new CustomGeneratorExample().makeImage(mol, 300, 300);
        ImageIO.write((RenderedImage)image, "PNG", new File("custom_above.png"));
    }

}
