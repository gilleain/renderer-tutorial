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

import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.Renderer;
import org.openscience.cdk.renderer.font.AWTFontManager;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator;
import org.openscience.cdk.renderer.generators.BasicBondGenerator;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.generators.RingGenerator;
import org.openscience.cdk.renderer.visitor.AWTDrawVisitor;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * Example showing use of the standard generators. Draw rings with and without 
 * rings in the center. 
 * 
 * @author maclean
 *
 */
public class StandardGeneratorsExample {
    
    public static Image makeImage(
            IMolecule molecule, int w, int h, List<IGenerator> generators) {
        Rectangle drawArea = new Rectangle(w, h);
        Image image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        
        Renderer renderer = new Renderer(generators, new AWTFontManager());
        renderer.setup(molecule, drawArea);
        renderer.getRenderer2DModel().setZoomFactor(1.5);
        renderer.getRenderer2DModel().setBondWidth(3);
        renderer.getRenderer2DModel().setAtomRadius(5);
        renderer.getRenderer2DModel().setShowAromaticity(true);
        
        Graphics2D g2 = (Graphics2D)image.getGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, w, h);
        
        renderer.paintMolecule(molecule, new AWTDrawVisitor(g2));
        return image;
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws IOException, CDKException {
        List<IGenerator> generatorsNoRings = new ArrayList<IGenerator>();
        generatorsNoRings.add(new BasicBondGenerator());
        generatorsNoRings.add(new BasicAtomGenerator());
        
        List<IGenerator> generatorsWithRings = new ArrayList<IGenerator>();
        generatorsWithRings.add(new RingGenerator());
        generatorsWithRings.add(new BasicAtomGenerator());
        
        IMolecule mol = MoleculeFactory.makeBiphenyl();
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        CDKHueckelAromaticityDetector.detectAromaticity(mol);
        StructureDiagramGenerator sdg = new StructureDiagramGenerator();
        sdg.setMolecule(mol);
        try {
            sdg.generateCoordinates();
        } catch (Exception e) { }
        mol = sdg.getMolecule();
        
        ImageIO.write(
                (RenderedImage)StandardGeneratorsExample.makeImage(
                        mol, 300, 300, generatorsNoRings), 
                        "PNG", new File("no_rings.png"));
        ImageIO.write(
                (RenderedImage)StandardGeneratorsExample.makeImage(
                        mol, 300, 300, generatorsWithRings), 
                        "PNG", new File("rings.png"));

    }

}
