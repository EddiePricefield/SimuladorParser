package template;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import static br.com.davidbuzatto.jsge.core.engine.EngineFrame.GRAY;
import br.com.davidbuzatto.jsge.geom.Rectangle;
import br.com.davidbuzatto.jsge.imgui.GuiComponent;
import br.com.davidbuzatto.jsge.imgui.GuiLabelButton;
import java.awt.Desktop;
import java.awt.Paint;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Nome do Projeto.
 * @author Eddie Pricefield
 * 
 * Engine JSGE
 * @author Prof. Dr. David Buzatto
 */
public class Main extends EngineFrame {
    
    //Componentes
    private List<GuiComponent> componentes;
    private GuiLabelButton btnLink;
    
    public Main() {
        
        super(
            800,                 // largura                      / width
            450,                 // algura                       / height
            "Simulador Parser",      // título                       / title
            60,                  // quadros por segundo desejado / target FPS
            true,                // suavização                   / antialiasing
            false,               // redimensionável              / resizable
            false,               // tela cheia                   / full screen
            false,               // sem decoração                / undecorated
            false,               // sempre no topo               / always on top
            false                // fundo invisível              / invisible background
        );
        
    }
    
    //----------< Criar >----------//
    
    @Override
    public void create() {
        
        useAsDependencyForIMGUI();
        componentes = new ArrayList<>();
        
        btnLink = new GuiLabelButton(getWidth() - 140, getHeight() - 65, 120, 20, "@EddiePricefield");
        
        componentes.add(btnLink);
        
    }
    
    //----------< Atualizar >----------//

    @Override
    public void update( double delta ) {
        
        atualizarComponentes(delta);
        
        if (btnLink.isMousePressed()) {

            try {
                URI link = new URI("https://github.com/EddiePricefield");
                Desktop.getDesktop().browse(link);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        
    }
    
    //----------< Desenhar >----------//
    
    @Override
    public void draw() {
        
        clearBackground( WHITE );

        String text = "Template JSGE - Eddie";
        String subtext = "Exemplo Simples de utilização da Engine!";
        Rectangle r = measureTextBounds( text, 40 );
        
        double x = getScreenWidth() / 2 - r.width / 2;
        double y = getScreenHeight() / 2 - r.height / 2;
        fillRectangle( x - 10, y, r.width + 20, r.height, BLACK );
        drawText( text, x, y + 10, 40, WHITE );
        drawOutlinedText(subtext, (int)x + 15, (int)y + 60, 20, ORANGE, 1, BLACK);
        
        desenharComponentes();
        
        drawFPS( 20, 20 );
    
    }
    
    //----------< Complementares >----------//    
    
    public void atualizarComponentes(double delta) {

        for (GuiComponent c : componentes) {
            if (!componentes.isEmpty()) {
                c.update(delta);
            }
        }

    }

    public void desenharComponentes() {

        for (GuiComponent c : componentes) {
            if (!componentes.isEmpty()) {
                c.draw();
            }
        }

    }

    public void drawOutlinedText(String text, int posX, int posY, int fontSize, Paint color, int outlineSize, Paint outlineColor) {
        drawText(text, posX - 2, posY + 2, fontSize, GRAY);
        drawText(text, posX - outlineSize, posY - outlineSize, fontSize, outlineColor);
        drawText(text, posX + outlineSize, posY - outlineSize, fontSize, outlineColor);
        drawText(text, posX - outlineSize, posY + outlineSize, fontSize, outlineColor);
        drawText(text, posX + outlineSize, posY + outlineSize, fontSize, outlineColor);
        drawText(text, posX, posY, fontSize, color);
    }
    
    //----------< Instanciar Engine e Iniciá-la >----------//

    public static void main( String[] args ) {
        new Main();
    }
    
}
