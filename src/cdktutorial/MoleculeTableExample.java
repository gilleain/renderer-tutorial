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
 * An example of rendering multiple molecules in separate panels. Each panel has
 * a reference to its own renderer, rather than having one renderer for all the
 * molecules. 
 * 
 * @author maclean
 *
 */
public class MoleculeTableExample extends JFrame {
    
    /**
     * Panel subclass to render a single molecule.
     *
     */
    public class MoleculeCell extends JPanel {
        
        private int preferredWidth;
        
        private int preferredHeight;
        
        private IAtomContainer atomContainer;
        
        private Renderer renderer;
        
        private boolean isNew;
        
        public MoleculeCell(IAtomContainer atomContainer, int w, int h) {
            this.atomContainer = atomContainer;
            this.preferredWidth = w;
            this.preferredHeight = h;
            
            this.setPreferredSize(new Dimension(w, h));
            this.setBackground(Color.WHITE);
            this.setBorder(BorderFactory.createEtchedBorder());
            
            List<IGenerator> generators = new ArrayList<IGenerator>();
            generators.add(new RingGenerator());
            generators.add(new BasicAtomGenerator());
            
            this.renderer = new Renderer(generators, new AWTFontManager());
            this.isNew = true;
        }
        
        public void paint(Graphics g) {
            super.paint(g);
            
            if (this.isNew) {
                Rectangle drawArea = 
                    new Rectangle(0, 0, this.preferredWidth, this.preferredHeight);
                this.renderer.setup(atomContainer, drawArea);
                this.isNew = false;
            }
            this.renderer.paintMolecule(
                    this.atomContainer,
                    new AWTDrawVisitor((Graphics2D) g));
        }
        
    }
    
    public class MoleculeTable extends JPanel {
        
        public MoleculeTable(List<IAtomContainer> atomContainers) {
            int w = 200;
            int h = 200;
            
            StructureDiagramGenerator sdg = new StructureDiagramGenerator();
            for (IAtomContainer atomContainer : atomContainers) {
                sdg.setMolecule((IMolecule)atomContainer);
                try {
                    sdg.generateCoordinates();
                    atomContainer = sdg.getMolecule();
                    this.add(new MoleculeCell(atomContainer, w, h));
                } catch (Exception e) {}
            }
        }
        
    }
    
    private MoleculeTable table;
    
    public MoleculeTableExample(List<IAtomContainer> atomContainers) {
        this.table = new MoleculeTable(atomContainers);
        this.add(new JScrollPane(this.table));
    }
    

    /**
     * @param args
     */
    public static void main(String[] args) {
        List<IAtomContainer> containers = new ArrayList<IAtomContainer>();
        containers.add(MoleculeFactory.make123Triazole());
        containers.add(MoleculeFactory.makeCyclobutadiene());
        containers.add(MoleculeFactory.makeTetrahydropyran());
        
        MoleculeTableExample example = new MoleculeTableExample(containers);
        
        example.pack();
        example.setVisible(true);
    }

}
