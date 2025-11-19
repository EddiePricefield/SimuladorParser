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
import parser.*;
import parser.ast.*;

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
    
    //Variáveis para o Parser
    private Parser parser;
    private Expressao expressaoResultado;
    private int currentRank;
    
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
        
        parser = Parser.parse("5 + 3 + 2");
        expressaoResultado = parser.getExpressaoResultante();
        
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

        desenharParser(100, 100, 50, 20);
        
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
    
    public void desenharParser( int x, int y, int spacing, int radius ) {
        currentRank = 0;
        calculateRanksAndLevels( expressaoResultado, 0 );
        drawEdges( expressaoResultado, x, y, spacing, radius );
        drawNodes( expressaoResultado, x, y, spacing, radius );
    }
    
    private void calculateRanksAndLevels(Expressao e, int level) {

        if (e instanceof Numero c) {
            c.rank = currentRank++;
            c.level = level;
        } else if (e instanceof ExpressaoAdicao a) {
            calculateRanksAndLevels(a.getOperandoE(), level + 1);
            a.rank = currentRank++;
            a.level = level;
            calculateRanksAndLevels(a.getOperandoD(), level + 1);
        } else if (e instanceof ExpressaoMultiplicacao m) {
            calculateRanksAndLevels(m.getOperandoE(), level + 1);
            m.rank = currentRank++;
            m.level = level;
            calculateRanksAndLevels(m.getOperandoD(), level + 1);
        }

    }

    private void drawNodes(Expressao e, int x, int y, int spacing, int radius) {

        fillCircle(x + e.rank * spacing, y + e.level * spacing, radius, WHITE);
        drawCircle(x + e.rank * spacing, y + e.level * spacing, radius, BLACK);

        if (e instanceof Numero c) {
            int w = measureText(c.toString(), 20);
            drawText(c.toString(), x + c.rank * spacing - w / 2 + 2, y + c.level * spacing - 5, 20, BLACK);
        } else if (e instanceof ExpressaoAdicao a) {
            int w = measureText(a.getValorOperador(), 20);
            drawText(a.getValorOperador(), x + a.rank * spacing - w / 2 + 2, y + a.level * spacing - 5, 20, BLACK);
            drawNodes(a.getOperandoE(), x, y, spacing, radius);
            drawNodes(a.getOperandoD(), x, y, spacing, radius);
        } else if (e instanceof ExpressaoMultiplicacao m) {
            int w = measureText(m.getValorOperador(), 20);
            drawText(m.getValorOperador(), x + m.rank * spacing - w / 2 + 2, y + m.level * spacing - 5, 20, BLACK);
            drawNodes(m.getOperandoE(), x, y, spacing, radius);
            drawNodes(m.getOperandoD(), x, y, spacing, radius);
        }

    }

    private void drawEdges(Expressao e, int x, int y, int spacing, int radius) {

        if (e instanceof ExpressaoAdicao a) {
            double x1 = x + a.rank * spacing;
            double y1 = y + a.level * spacing;
            double x2 = x + a.getOperandoE().rank * spacing;
            double y2 = y + a.getOperandoE().level * spacing;
            double x3 = x + a.getOperandoD().rank * spacing;
            double y3 = y + a.getOperandoD().level * spacing;
            drawLine(x1, y1, x2, y2, BLACK);
            drawLine(x1, y1, x3, y3, BLACK);
            drawEdges(a.getOperandoE(), x, y, spacing, radius);
            drawEdges(a.getOperandoD(), x, y, spacing, radius);
        } else if (e instanceof ExpressaoMultiplicacao m) {
            double x1 = x + m.rank * spacing;
            double y1 = y + m.level * spacing;
            double x2 = x + m.getOperandoE().rank * spacing;
            double y2 = y + m.getOperandoE().level * spacing;
            double x3 = x + m.getOperandoD().rank * spacing;
            double y3 = y + m.getOperandoD().level * spacing;
            drawLine(x1, y1, x2, y2, EngineFrame.BLACK);
            drawLine(x1, y1, x3, y3, EngineFrame.BLACK);
            drawEdges(m.getOperandoE(), x, y, spacing, radius);
            drawEdges(m.getOperandoD(), x, y, spacing, radius);
        }

    }
    
    //----------< Instanciar Engine e Iniciá-la >----------//

    public static void main( String[] args ) {
        new Main();
    }
    
}
