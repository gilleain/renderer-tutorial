package cdktutorial;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.Renderer;
import org.openscience.cdk.renderer.font.AWTFontManager;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.generators.RingGenerator;
import org.openscience.cdk.renderer.visitor.AWTDrawVisitor;
import org.openscience.cdk.templates.MoleculeFactory;

/**
 * Example code for implementing a scrolling panel.
 * 
 * @author maclean
 *
 */
public class ScrollPaneExample extends JFrame {
    
    public class MoleculePanel extends JPanel {
        
        private int initialWidth;
        
        private int initialHeight;
        
        private Renderer renderer;
        
        private IAtomContainer atomContainer;
        
        private boolean isNew;
        
        public MoleculePanel(IAtomContainer atomContainer) {
            this.atomContainer = atomContainer;
            
            this.initialWidth = 200;
            this.initialHeight = 200;
            
            this.setPreferredSize(
                    new Dimension(this.initialWidth, this.initialHeight));
            this.setBackground(Color.WHITE);
            this.setBorder(BorderFactory.createRaisedBevelBorder());
            
            List<IGenerator> generators = new ArrayList<IGenerator>();
            generators.add(new RingGenerator());
            generators.add(new BasicAtomGenerator());
            
            this.renderer = new Renderer(generators, new AWTFontManager());
            this.isNew = true;
        }
        
        public void paint(Graphics g) {
            super.paint(g);
            
            Rectangle drawArea = 
                new Rectangle(0, 0, this.initialWidth, this.initialHeight);
            
            if (this.isNew) {
                this.renderer.setup(atomContainer, drawArea);
                this.isNew = false;
            }
            
            Rectangle diagramRectangle = 
                this.renderer.calculateDiagramBounds(atomContainer);
            
            Rectangle result = renderer.shift(drawArea, diagramRectangle);
            this.setPreferredSize(new Dimension(result.width, result.height));
            this.revalidate();
            
            this.renderer.paintMolecule(
                    this.atomContainer,
                    new AWTDrawVisitor((Graphics2D) g));
        }
    }
    
    public ScrollPaneExample() {
        IMolecule chain = MoleculeFactory.makeEthylPropylPhenantren();
        
        StructureDiagramGenerator sdg = new StructureDiagramGenerator();
        sdg.setMolecule((IMolecule)chain);
        
        try {
            sdg.generateCoordinates();
            MoleculePanel molPanel = new MoleculePanel(sdg.getMolecule()); 
            this.add(new JScrollPane(molPanel));
        } catch (Exception e) {}
        
        this.pack();
        this.setVisible(true);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        new ScrollPaneExample();
    }

}
