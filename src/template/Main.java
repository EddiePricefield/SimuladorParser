package template;

import br.com.davidbuzatto.jsge.core.Camera2D;
import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import static br.com.davidbuzatto.jsge.core.engine.EngineFrame.GRAY;
import static br.com.davidbuzatto.jsge.core.engine.EngineFrame.KEY_DOWN;
import static br.com.davidbuzatto.jsge.core.engine.EngineFrame.KEY_LEFT;
import static br.com.davidbuzatto.jsge.core.engine.EngineFrame.KEY_RIGHT;
import static br.com.davidbuzatto.jsge.core.engine.EngineFrame.KEY_UP;
import static br.com.davidbuzatto.jsge.core.engine.EngineFrame.LIGHTGRAY;
import br.com.davidbuzatto.jsge.imgui.GuiButton;
import br.com.davidbuzatto.jsge.imgui.GuiComponent;
import br.com.davidbuzatto.jsge.imgui.GuiLabelButton;
import br.com.davidbuzatto.jsge.imgui.GuiTextField;
import br.com.davidbuzatto.jsge.math.Vector2;
import java.awt.Color;
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
    
    private Color tema = new Color(0xFF738ebd);
    private Color background = new Color (0xFFc9c9c9);
    
    //Componentes
    private List<GuiComponent> componentes;
    
    private GuiLabelButton btnLink;
    private GuiTextField textFieldExpressao;
    private GuiButton btnInserir;
    
    //Câmera
    private Camera2D camera;
    private Vector2 cameraPos;
    private final double cameraVel = 300;
    private GuiButton btnCamE;
    private GuiButton btnCamD;
    private GuiButton btnCamB;
    private GuiButton btnCamC;
    private GuiButton btnCamReset;
    private GuiButton btnCamMais;
    private GuiButton btnCamMenos;
    
    //Variáveis para o Parser
    private Parser parser;
    private Expressao expressaoResultado;
    private int currentRank;
    private String resultadoDaExpressao;
    
    public Main() {
        
        super(
            800,                 // largura                      / width
            450,                 // altura                       / height
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
        
        parser = Parser.parse("");
        expressaoResultado = parser.getExpressaoResultante();
        
        textFieldExpressao = new GuiTextField(165, 400, 500, 30, "");
        btnInserir = new GuiButton(685, 400, 100, 30, "Inserir");
        
        btnCamC = new GuiButton(19, 407, 10, 10, "");
        btnCamB = new GuiButton(19, 429, 10, 10, "");
        btnCamE = new GuiButton(8, 418, 10, 10, "");
        btnCamD = new GuiButton(30, 418, 10, 10, "");
        btnCamReset = new GuiButton(21, 420, 6, 6, "");
        btnCamMais = new GuiButton(138, 410, 8, 8, "");
        btnCamMenos = new GuiButton(138, 425, 8, 8, "");
        
        componentes.add(textFieldExpressao);
        componentes.add(btnInserir);
        componentes.add(btnCamC);
        componentes.add(btnCamB);
        componentes.add(btnCamE);
        componentes.add(btnCamD);
        componentes.add(btnCamReset);
        componentes.add(btnCamMais);
        componentes.add(btnCamMenos);
        
        //Criação da Câmera
        camera = new Camera2D();
        resetarCamera();        
        btnLink = new GuiLabelButton(10, getHeight() - 65, 120, 20, "@EddiePricefield");
        
    }
    
    //----------< Atualizar >----------//

    @Override
    public void update( double delta ) {
        
        atualizarComponentes(delta);
        
        //Cores
        textFieldExpressao.setBackgroundColor(background);
        
        //Inserir a Expressão do Parser
        if (isKeyDown(KEY_ENTER) || btnInserir.isMousePressed()){
            parser = Parser.parse(textFieldExpressao.getValue());
            expressaoResultado = parser.getExpressaoResultante();
            resetarCamera();
            resultadoDaExpressao = Double.toString(parser.getResultado());
        }
        
        //Joystick (Movimento da Câmera)
        Color fundoBotao = LIGHTGRAY;
        Color cliqueBotao = new Color(151, 232, 255, 255);

        if (isKeyDown(KEY_UP) || btnCamC.isMouseDown()) {
            cameraPos.y += cameraVel * delta;
            btnCamC.setBackgroundColor(cliqueBotao);
        } else {
            btnCamC.setBackgroundColor(fundoBotao);
        }

        if (isKeyDown(KEY_DOWN) || btnCamB.isMouseDown()) {
            cameraPos.y -= cameraVel * delta;
            btnCamB.setBackgroundColor(cliqueBotao);
        } else {
            btnCamB.setBackgroundColor(fundoBotao);
        }

        if (isKeyDown(KEY_LEFT) || btnCamE.isMouseDown()) {
            cameraPos.x += cameraVel * delta;
            btnCamE.setBackgroundColor(cliqueBotao);
        } else {
            btnCamE.setBackgroundColor(fundoBotao);
        }

        if (isKeyDown(KEY_RIGHT) || btnCamD.isMouseDown()) {
            cameraPos.x -= cameraVel * delta;
            btnCamD.setBackgroundColor(cliqueBotao);
        } else {
            btnCamD.setBackgroundColor(fundoBotao);
        }
        
        //Resetar Câmera
        if (isKeyDown(KEY_R) || btnCamReset.isMousePressed()) {
            resetarCamera();
            btnCamReset.setBackgroundColor(cliqueBotao);
        } else {
            btnCamReset.setBackgroundColor(fundoBotao);
        }
        
        //Zoom da Câmera
        if (getMouseWheelMove() < 0 || btnCamMenos.isMouseDown()) {
            camera.zoom -= 1 * delta;
            btnCamMenos.setBackgroundColor(cliqueBotao);
        } else {
            btnCamMenos.setBackgroundColor(fundoBotao);
        }

        if (getMouseWheelMove() > 0 || btnCamMais.isMouseDown()) {
            camera.zoom += 1 * delta;
            btnCamMais.setBackgroundColor(cliqueBotao);
        } else {
            btnCamMais.setBackgroundColor(fundoBotao);
        }
        
        //Atualizar Câmera
        camera.target = new Vector2(cameraPos.x, cameraPos.y);
        
        //Link Github
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
        
        clearBackground( background );
        
        //Desenhar o Parser
        beginMode2D(camera);
        desenharParser(100, 100, 50, 20);
        endMode2D();
        
        //Desenhar o menu em cima
        fillTriangle(610, 30, 900, 100, 800, -80, tema);
        drawTriangle(610, 30, 900, 100, 800, -80, BLACK);
        fillRectangle(-1, -1, 801, 50, tema);
        fillTriangle(635, 26, 890, 85, 815, -70, background);
        drawTriangle(635, 26, 890, 85, 815, -70, BLACK);
        drawLine(0, 49, 690, 49, BLACK);
        
        //Desenhar menu em baixo
        fillCircle(85, 370, 65, tema);
        drawCircle(85, 370, 65, BLACK);
        fillRectangle(0, 380, 800, 100, tema);
        fillCircle(85, 370, 55, background);
        drawCircle(85, 370, 55, BLACK);
        drawLine(0, 380, 20, 380, BLACK);
        drawLine(150, 380, 800, 380, BLACK);
        
        //Desenhar o Título do Parser
        drawOutlinedText("PARSER", 690, 8, 15 , 28, tema, 1, BLACK);
        
        //Desenhar o Resultado
        if (resultadoDaExpressao != null){
            
            double num = Double.parseDouble(resultadoDaExpressao);
            
            if( num % (int)num == 0 || num == 0){
                resultadoDaExpressao = String.valueOf((int) num);
            }
            
            int tam = resultadoDaExpressao.length();
            
            switch (tam){
                case 1 -> drawOutlinedText(resultadoDaExpressao, 58, 335, 100, tema, 1, BLACK);
                case 2 -> drawOutlinedText(resultadoDaExpressao, 38, 345, 80, tema, 1, BLACK);
                case 3 -> drawOutlinedText(resultadoDaExpressao, 36, 355, 55, tema, 1, BLACK);
                case 4 -> drawOutlinedText(resultadoDaExpressao, 38, 358, 40, tema, 1, BLACK);
                case 5 -> drawOutlinedText(resultadoDaExpressao, 36, 360, 33, tema, 1, BLACK);
                case 6 -> drawOutlinedText(resultadoDaExpressao, 38, 363, 27, tema, 1, BLACK);
                case 7 -> drawOutlinedText(resultadoDaExpressao, 36, 363, 24, tema, 1, BLACK);
                case 8 -> drawOutlinedText(resultadoDaExpressao, 33, 363, 22, tema, 1, BLACK);
                case 9 -> drawOutlinedText(resultadoDaExpressao, 36, 363, 19, tema, 1, BLACK);
            }

        }
        
        //Desenhar os Componentes
        desenharComponentes();
    
    }
    
    //----------< Complementares >----------//    
    
    public void atualizarComponentes(double delta) {

        for (GuiComponent c : componentes) {
            if (!componentes.isEmpty()) {
                c.update(delta);
                c.setBorderColor(BLACK);
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
    
    
    public void drawOutlinedText(String text, int posX, int posY, int fontSize, int rotation, Paint color, int outlineSize, Paint outlineColor){
        drawText(text, posX - 2, posY + 2, fontSize, rotation, GRAY);
        drawText(text, posX - outlineSize, posY - outlineSize, fontSize, rotation, outlineColor);
        drawText(text, posX + outlineSize, posY - outlineSize, fontSize, rotation, outlineColor);
        drawText(text, posX - outlineSize, posY + outlineSize, fontSize, rotation, outlineColor);
        drawText(text, posX + outlineSize, posY + outlineSize, fontSize, rotation, outlineColor);
        drawText(text, posX, posY, fontSize, rotation, color);
    }

    public void drawOutlinedText(String text, int posX, int posY, int fontSize, Paint color, int outlineSize, Paint outlineColor) {
        drawText(text, posX - 2, posY + 2, fontSize, GRAY);
        drawText(text, posX - outlineSize, posY - outlineSize, fontSize, outlineColor);
        drawText(text, posX + outlineSize, posY - outlineSize, fontSize, outlineColor);
        drawText(text, posX - outlineSize, posY + outlineSize, fontSize, outlineColor);
        drawText(text, posX + outlineSize, posY + outlineSize, fontSize, outlineColor);
        drawText(text, posX, posY, fontSize, color);
    }
    
    public void resetarCamera(){
        currentRank = 0;
        calculateRanksAndLevels(expressaoResultado, 0);
        
        if (expressaoResultado != null){
            cameraPos = new Vector2(110 + expressaoResultado.rank * 50, 220 + expressaoResultado.level * 50);
            camera.target = cameraPos;
            camera.offset = new Vector2(getWidth() / 2, getHeight() / 2);
            camera.rotation = 0;
            camera.zoom = 1;
        }else{
            cameraPos = new Vector2(0, 0);
        }
        
    }
    
    public void desenharParser( int x, int y, int spacing, int radius ) {
        
        if (expressaoResultado != null){
            currentRank = 0;
            calculateRanksAndLevels(expressaoResultado, 0);
            desenharArestas(expressaoResultado, x, y, spacing, radius);
            desenharNos(expressaoResultado, x, y, spacing, radius);
        }
        
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

    private void desenharNos(Expressao e, int x, int y, int spacing, int radius) {

        fillCircle(x + e.rank * spacing, y + e.level * spacing, radius, tema);
        drawCircle(x + e.rank * spacing, y + e.level * spacing, radius, BLACK);

        if (e instanceof Numero c) {
            int w = measureText(c.toString(), 20);
            drawText(c.toString(), 1 + x + c.rank * spacing - w / 2 + 2, y + c.level * spacing - 5, 17, BLACK);
        } else if (e instanceof ExpressaoAdicao a) {
            int w = measureText(a.getValorOperador(), 20);
            drawText(a.getValorOperador(), 1 + x + a.rank * spacing - w / 2 + 2, y + a.level * spacing - 5, 17, BLACK);
            desenharNos(a.getOperandoE(), x, y, spacing, radius);
            desenharNos(a.getOperandoD(), x, y, spacing, radius);
        } else if (e instanceof ExpressaoMultiplicacao m) {
            int w = measureText(m.getValorOperador(), 20);
            drawText(m.getValorOperador(), 1 + x + m.rank * spacing - w / 2 + 2, y + m.level * spacing - 5, 17, BLACK);
            desenharNos(m.getOperandoE(), x, y, spacing, radius);
            desenharNos(m.getOperandoD(), x, y, spacing, radius);
        }

    }

    private void desenharArestas(Expressao e, int x, int y, int spacing, int radius) {

        if (e instanceof ExpressaoAdicao a) {
            double x1 = x + a.rank * spacing;
            double y1 = y + a.level * spacing;
            double x2 = x + a.getOperandoE().rank * spacing;
            double y2 = y + a.getOperandoE().level * spacing;
            double x3 = x + a.getOperandoD().rank * spacing;
            double y3 = y + a.getOperandoD().level * spacing;
            drawLine(x1, y1, x2, y2, BLACK);
            drawLine(x1, y1, x3, y3, BLACK);
            desenharArestas(a.getOperandoE(), x, y, spacing, radius);
            desenharArestas(a.getOperandoD(), x, y, spacing, radius);
        } else if (e instanceof ExpressaoMultiplicacao m) {
            double x1 = x + m.rank * spacing;
            double y1 = y + m.level * spacing;
            double x2 = x + m.getOperandoE().rank * spacing;
            double y2 = y + m.getOperandoE().level * spacing;
            double x3 = x + m.getOperandoD().rank * spacing;
            double y3 = y + m.getOperandoD().level * spacing;
            drawLine(x1, y1, x2, y2, EngineFrame.BLACK);
            drawLine(x1, y1, x3, y3, EngineFrame.BLACK);
            desenharArestas(m.getOperandoE(), x, y, spacing, radius);
            desenharArestas(m.getOperandoD(), x, y, spacing, radius);
        }

    }
    
    //----------< Instanciar Engine e Iniciá-la >----------//

    public static void main( String[] args ) {
        new Main();
    }
    
}
