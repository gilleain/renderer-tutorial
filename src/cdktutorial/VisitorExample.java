package cdktutorial;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
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
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.LineElement;
import org.openscience.cdk.renderer.font.AWTFontManager;
import org.openscience.cdk.renderer.font.IFontManager;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator;
import org.openscience.cdk.renderer.generators.BasicBondGenerator;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.visitor.IDrawVisitor;
import org.openscience.cdk.templates.MoleculeFactory;

public class VisitorExample {
    
    public class ShadowVisitor implements IDrawVisitor {
        
        private Graphics2D g;
        
        private AffineTransform transform;
        
        private RendererModel rendererModel;
        
        private int dx;
        
        private int dy;
        
        public ShadowVisitor(Graphics2D g, int dx, int dy) {
            this.g = g;
            this.dx = dx;
            this.dy = dy;
        }
        
        public void visit(IRenderingElement element) {
            if (element instanceof ElementGroup) {
                this.visitElementGroup((ElementGroup)element);
            } else if (element instanceof LineElement) {
                this.visitLineElement((LineElement) element);
            } 
        }
        
        public void visitElementGroup(ElementGroup group) {
            group.visitChildren(this);
        }
        
        public void visitLineElement(LineElement line) {
            int w = (int) (line.width * this.rendererModel.getScale());
            this.g.setStroke(new BasicStroke(w));
            int[] a = this.transformPoint(line.x1, line.y1);
            int[] b = this.transformPoint(line.x2, line.y2);
            this.g.drawLine(a[0] + dx, a[1] + dy, b[0] + dx, b[1] + dy);
        }

        public void setFontManager(IFontManager fontManager) {
            // not drawing text, so don't need it
        }

        public void setRendererModel(RendererModel rendererModel) {
            this.rendererModel = rendererModel;
        }

        public void setTransform(AffineTransform transform) {
            this.transform = transform;
        }
        
        public int[] transformPoint(double x, double y) {
            double[] src = new double[] {x, y};
            double[] dest = new double[2];
            this.transform.transform(src, 0, dest, 0, 1);
            return new int[] { (int) dest[0], (int) dest[1] };
        }
        
    }
    
    public void draw(IMolecule molecule) {
        List<IGenerator> generators = new ArrayList<IGenerator>();
        generators.add(new BasicBondGenerator());
        generators.add(new BasicAtomGenerator());
        Renderer renderer = new Renderer(generators, new AWTFontManager());
        renderer.getRenderer2DModel().setBondWidth(3);
        
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
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, WIDTH, HEIGHT);
        
        g2.setColor(Color.LIGHT_GRAY);
        renderer.paintMolecule(molecule, new ShadowVisitor(g2, 8, 8));
        g2.setColor(Color.BLACK);
        renderer.paintMolecule(molecule, new ShadowVisitor(g2, 0, 0));
        try {
            ImageIO.write((RenderedImage)image, "PNG", new File("shadow.png"));
        } catch (IOException ioe) {
            
        }
    }
    
    public static void main(String[] args) {
        IMolecule molecule = MoleculeFactory.makePhenylEthylBenzene();
        new VisitorExample().draw(molecule);
    }

}
