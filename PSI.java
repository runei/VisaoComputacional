import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;
import java.math.*;

// ---------------------------------------------------------------
// Classe que cria uma Frame principal, onde se situam os comandos
// de manipulação de imagem. Implementa a interface ActionListener
// para lidar com os eventos produzidos pelos botões.
// ---------------------------------------------------------------
class PSI extends Frame implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	// Variáveis globais de apoio
	// Atenção: E se eu quiser ter múltiplas imagens?
	// Isto deve estar na classe ImageFrame!
	private Image image;
	private int sizex;
	private int sizey;;
	private int matrix[];
	ImagePanel imagePanel; // E se eu quiser múltiplas janelas?
	
	// Função main cria uma instance dinâmica da classe
	public static void main(String args[])
	{
		new PSI();
	}

	// Construtor
	public PSI()
	{
		// Lidar com o evento de Fechar Janela
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		// Sinalizar que não existe nenhum ImagePanel
		imagePanel = null;
		
		// Criar botões 
		this.setLayout(new GridLayout(15,1,1,1));
		
		Button button = new Button("Abrir Ficheiro");
		button.setVisible(true);
		button.addActionListener(this);
		add(button);		

		button = new Button("Manipular Imagem");
		button.setVisible(true);
		button.addActionListener(this);
		add(button);

		button = new Button("Visualizar RGB");
		button.setVisible(true);
		button.addActionListener(this);
		add(button);		
		
		button = new Button("Visualizar Hue");
		button.setVisible(true);
		button.addActionListener(this);
		add(button);

		button = new Button("Visualizar Sat");
		button.setVisible(true);
		button.addActionListener(this);
		add(button);	

		button = new Button("Visualizar Value");
		button.setVisible(true);
		button.addActionListener(this);
		add(button);	

		button = new Button("Colorir Imagem");
		button.setVisible(true);
		button.addActionListener(this);
		add(button);	

		button = new Button("Contrast");
		button.setVisible(true);
		button.addActionListener(this);
		add(button);	

		button = new Button("Histogram Equalization");
		button.setVisible(true);
		button.addActionListener(this);
		add(button);	

		button = new Button("Filtro Media");
		button.setVisible(true);
		button.addActionListener(this);
		add(button);	

		button = new Button("Sobel");
		button.setVisible(true);
		button.addActionListener(this);
		add(button);	

		button = new Button("Guardar Imagem");
		button.setVisible(true);
		button.addActionListener(this);
		add(button);		
		
		pack();
		
		// Janela principal 	
		setLocation(100,100);
		setSize(100,350);
		setVisible(true);
	}
	
	
	// O utilizador carregou num botão
	public void actionPerformed (ActionEvent myEvent)
	{
		// Qual o botão premido?
		Button pressedButton = (Button)myEvent.getSource();
		String nomeBotao = pressedButton.getActionCommand();

		// Realizar acção adequada
		if (nomeBotao.equals("Abrir Ficheiro")) abrirFicheiro();
		else if (nomeBotao.equals("Manipular Imagem")) manipularImagem();
		else if (nomeBotao.equals("Visualizar RGB")) visualizarRGB();
		else if (nomeBotao.equals("Visualizar Hue")) visualizarHue();
		else if (nomeBotao.equals("Visualizar Sat")) visualizarSat();
		else if (nomeBotao.equals("Visualizar Value")) visualizarValue();
		else if (nomeBotao.equals("Colorir Imagem")) colorirImagem();
		else if (nomeBotao.equals("Contrast")) contrast();
		else if (nomeBotao.equals("Histogram Equalization")) histogramEqualization();
		else if (nomeBotao.equals("Filtro Media")) filtroMedia();
		else if (nomeBotao.equals("Sobel")) sobel();
		else if (nomeBotao.equals("Guardar Imagem")) guardarImagem();
	}
	
	// Abrir um ficheiro de Imagem
	private void abrirFicheiro()
	{
		// Load Image - Escolher nome da imagem a carregar!
		// Bem mais interessante usar uma interface gráfica para isto...
		LoadImage("lena.jpg");

		// Obter matriz da imagem
		// A variável "matrix" fica com os valores de cada pixel da imagem
		// A dimensão desta é determinada por "sizex" e "sizey"
		// Cada valor têm 4 bytes. Estes correspondem invidividualmente a:
		// Transparência, Vermelho, Verde, Azul
		// Para aceder aos valores individuais:
		//		red = (color >> 16) & 0xff;
		//	    green = (color >> 8) & 0xff;
		//		blue = color & 0xff;
		sizex = image.getWidth(null);
		sizey = image.getHeight(null);
		matrix = new int[sizex*sizey];
		PixelGrabber pg = new PixelGrabber(image, 0, 0, sizex, sizey, matrix, 0, sizex);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			System.err.println("interrupted waiting for pixels!");
			return;
		}
		if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
			System.err.println("image fetch aborted or errored");
			return;
		}
		
		// Visualizar imagem - Usar um Frame externo
		if (imagePanel==null) imagePanel = new ImagePanel(image);
		else imagePanel.newImage(image);
		imagePanel.setLocation(300, 200);
		imagePanel.setSize(image.getWidth(null),image.getHeight(null));
		imagePanel.setVisible(true);
	}

	public void converterPretoBranco()
	{
		// Exemplo: Conversão de uma imagem a cores, para uma imagem a preto e branco

		// Variáveis de apoio
		int verde, vermelho, azul, cinzento;
		int x;

		// Ciclo que percorre a imagem inteira
		for (x=0; x < sizex*sizey; x++)
		{
			vermelho = getRed(matrix[x]);
			verde = getGreen(matrix[x]);
			azul = getBlue(matrix[x]);
			
			// Calcular luminosidade
			cinzento = (vermelho+verde+azul)/3;
			
			// Criar valor de cor
			matrix[x] = makeColor(cinzento, cinzento, cinzento);
		}
		
		// Após a manipulaçao da matrix, é necessário criar o objecto gráfico (image) 
		image = createImage(new MemoryImageSource(sizex, sizey, matrix, 0, sizex));
		
		// Carregar a imagem no painel externo de visualização
		imagePanel.newImage(image);
	}

	public void inverterImagem()
	{
		// Exemplo: Conversão de uma imagem a cores, para uma imagem a preto e branco

		// Variáveis de apoio
		int verde, vermelho, azul;
		int x;

		// Ciclo que percorre a imagem inteira
		for (x=0; x < sizex*sizey; x++)
		{
			vermelho = 255 - getRed(matrix[x]);
			verde = 255 - getGreen(matrix[x]);
			azul = 255 - getBlue(matrix[x]);

			// Criar valor de cor
			matrix[x] = makeColor(vermelho, verde, azul);
		}
		
		// Após a manipulaçao da matrix, é necessário criar o objecto gráfico (image) 
		image = createImage(new MemoryImageSource(sizex, sizey, matrix, 0, sizex));
		
		// Carregar a imagem no painel externo de visualização
		imagePanel.newImage(image);
	}

	// Exemplo de uma função que manipula a imagem
	public void manipularImagem()
	{
		converterPretoBranco();
		inverterImagem();
	}

	public void visualizarRGB()
	{
		int verde, vermelho, azul;
		int i;

		for (i = 0; i < sizex * sizey; i++)
		{
			vermelho = getRed(matrix[i]);
			verde = getGreen(matrix[i]);
			azul = getBlue(matrix[i]);	

			// Criar valor de cor
			matrix[i] = makeColor(0, 0, azul);
		}
		
		// Após a manipulaçao da matrix, é necessário criar o objecto gráfico (image) 
		image = createImage(new MemoryImageSource(sizex, sizey, matrix, 0, sizex));
		
		// Carregar a imagem no painel externo de visualização
		imagePanel.newImage(image);
	}

	public int getTheta(int red, int green, int blue)
	{
		double up = 1 / 2 * ((red - green) + (red - blue));
		double down = Math.pow(Math.pow(red - green, 2) + (red - blue) * (green - blue), 1/2);
		int result = (int) Math.toDegrees(Math.acos(up / down));
		return result;
	}

	public void visualizarHue()
	{
		int verde, vermelho, azul;
		int i;

		for (i = 0; i < sizex * sizey; i++)
		{
			vermelho = getRed(matrix[i]);
			verde = getGreen(matrix[i]);
			azul = getBlue(matrix[i]);	

			int hue = getTheta(vermelho, verde, azul); //hue = theta if B <= G

			if (azul > verde)
			{
				hue = 360 - hue;
			}


			// Criar valor de cor
			matrix[i] = makeColor(hue, hue, hue);
		}
		
		// Após a manipulaçao da matrix, é necessário criar o objecto gráfico (image) 
		image = createImage(new MemoryImageSource(sizex, sizey, matrix, 0, sizex));
		
		// Carregar a imagem no painel externo de visualização
		imagePanel.newImage(image); 
	}

	public void visualizarSat()
	{
		int green, red, blue;
		int i;

		for (i = 0; i < sizex * sizey; i++)
		{
			red = getRed(matrix[i]);
			green = getGreen(matrix[i]);
			blue = getBlue(matrix[i]);	

			double sat = 1.0 - (3.0 / (red + green + blue)) * Math.min(Math.min(red, green), blue);

			int color =  (int) (255 * sat);

			// Criar valor de cor
			matrix[i] = makeColor(color, color, color);
		}
		
		// Após a manipulaçao da matrix, é necessário criar o objecto gráfico (image) 
		image = createImage(new MemoryImageSource(sizex, sizey, matrix, 0, sizex));
		
		// Carregar a imagem no painel externo de visualização
		imagePanel.newImage(image); 
	}

	public void visualizarValue()
	{
		int green, red, blue;
		int i;

		for (i = 0; i < sizex * sizey; i++)
		{
			red = getRed(matrix[i]);
			green = getGreen(matrix[i]);
			blue = getBlue(matrix[i]);	

			int value = (red + green + blue) / 3;

			// Criar valor de cor
			matrix[i] = makeColor(value, value, value);
		}
		
		// Após a manipulaçao da matrix, é necessário criar o objecto gráfico (image) 
		image = createImage(new MemoryImageSource(sizex, sizey, matrix, 0, sizex));
		
		// Carregar a imagem no painel externo de visualização
		imagePanel.newImage(image); 
	}

	public void colorirImagem()
	{
		int green, red, blue;
		int i, j, n_colors = 8;
		int split = 256 / n_colors; // 6 colors

		int[][] colors = new int[][] {
			{ 255, 0, 0 },
			{ 255, 0, 255 },
			{ 128, 0, 255 },
			{ 0, 0, 255 },
			{ 0, 255, 255 },
			{ 0, 255, 0 },
			{ 255, 255, 0 },
			{ 255, 128, 0 },
		};

		for (i = 0; i < sizex * sizey; i++)
		{
			red = getRed(matrix[i]);
			green = getGreen(matrix[i]);
			blue = getBlue(matrix[i]);	

			int value = (red + green + blue) / 3;

			for (j = 0; j < n_colors; ++j)
			{
				if (value < (j+1) * split)
				{
					red = colors[j][0];
					green = colors[j][1];
					blue = colors[j][2];
					break;
				}
			}

			// Criar valor de cor
			matrix[i] = makeColor(red, green, blue);
		}
		
		// Após a manipulaçao da matrix, é necessário criar o objecto gráfico (image) 
		image = createImage(new MemoryImageSource(sizex, sizey, matrix, 0, sizex));
		
		// Carregar a imagem no painel externo de visualização
		imagePanel.newImage(image); 
	}

	public void contrast()
	{
		int green, red, blue;
		int i;
		int[] histogram = new int[256];
		
		for (i = 0; i < sizex * sizey; i++)
		{
			red = getRed(matrix[i]);
			green = getGreen(matrix[i]);
			blue = getBlue(matrix[i]);	

			int value = (red + green + blue) / 3;

			histogram[value]++;
		}

		int min = 0, max = 255;
		i = min;
		while (histogram[i] == 0)
		{
			min++;
			i++;
		}

		i = max;
		while (histogram[i] == 0)
		{
			max--;
			i--;
		}

		int discart = (int) (0.05 * sizex * sizey);
		int tmpMin = discart;
		while (tmpMin > 0)
		{
			tmpMin -= histogram[min];
			min++;
		}

		int tmpMax = discart;
		while (tmpMax > 0)
		{
			tmpMax -= histogram[max];
			max--;
		}

		for (i = 0; i < sizex * sizey; i++)
		{
			red = getRed(matrix[i]);
			green = getGreen(matrix[i]);
			blue = getBlue(matrix[i]);	

			int value = (red + green + blue) / 3;

			int new_val = (int) (255 * ((double)(value - min) / (max - min)));

			if (new_val > 255)
			{
				new_val = 255;
			}
			else if (new_val < 0)
			{
				new_val = 0;
			}
			matrix[i] = makeColor(new_val, new_val, new_val);			

		}

		// Após a manipulaçao da matrix, é necessário criar o objecto gráfico (image) 
		image = createImage(new MemoryImageSource(sizex, sizey, matrix, 0, sizex));
		
		// Carregar a imagem no painel externo de visualização
		imagePanel.newImage(image); 
	}

	public void histogramEqualization()
	{
		int green, red, blue;
		int i;
		int[] histogram = new int[256];
		double[] cumulative_histogram = new double[256];
		
		for (i = 0; i < sizex * sizey; i++)
		{
			red = getRed(matrix[i]);
			green = getGreen(matrix[i]);
			blue = getBlue(matrix[i]);	

			int value = (red + green + blue) / 3;

			histogram[value]++;
		}

		cumulative_histogram[0] = histogram[0];
		for (i = 1; i < 256; ++i)
		{
			cumulative_histogram[i] = cumulative_histogram[i-1] + histogram[i];
		}

		for (i = 0; i < sizex * sizey; i++)
		{
			red = getRed(matrix[i]);
			green = getGreen(matrix[i]);
			blue = getBlue(matrix[i]);	

			int value = (red + green + blue) / 3;

			int new_val =(int) Math.round((cumulative_histogram[value] / (sizex * sizey) * 255.0));
			
			matrix[i] = makeColor(new_val, new_val, new_val);			

		}

		// Após a manipulaçao da matrix, é necessário criar o objecto gráfico (image) 
		image = createImage(new MemoryImageSource(sizex, sizey, matrix, 0, sizex));
		
		// Carregar a imagem no painel externo de visualização
		imagePanel.newImage(image); 

	}

	public int getValue(int pos)
	{
		int green, red, blue;

		red = getRed(matrix[pos]);
		green = getGreen(matrix[pos]);
		blue = getBlue(matrix[pos]);

		int value = (red + green + blue) / 3;
		return value;
	}

	public void filtroMedia()
	{
		int green, red, blue;
		int i;
		
		for (i = 0; i < sizex * sizey; i++)
		{
			int value = getValue(i);
			int new_val;
			if (i % sizex == 0 || (i+1) % sizex == 0 || i < sizex || i >= sizex*sizey-sizex)
			{
				new_val = value;
			}
			else
			{
				new_val = (getValue(i-sizex-1) + getValue(i-sizex) + getValue(i-sizex+1) + getValue(i-1) + getValue(i) + getValue(i+1) + getValue(i+sizex-1) + getValue(i+sizex) + getValue(i+sizex+1)) / 9;
			}

			matrix[i] = makeColor(new_val, new_val, new_val);			

		}

		// Após a manipulaçao da matrix, é necessário criar o objecto gráfico (image) 
		image = createImage(new MemoryImageSource(sizex, sizey, matrix, 0, sizex));
		
		// Carregar a imagem no painel externo de visualização
		imagePanel.newImage(image); 

	}

	public void sobel()
	{
		int green, red, blue;
		int i;
		int[] mask_x = new int[] {
			-1, 0, 1, -2, 0, 2, -1, 0, 1	
		};
		int[] mask_y = new int[] {
			1, 2, 1, 0, 0, 0, -1, -2, -1
		};

		for (i = 0; i < sizex*sizey; i++)
		{
			int value = getValue(i);
			int new_val;
			if (i % sizex == 0 || (i+1) % sizex == 0 || i < sizex || i >= sizex*sizey-sizex)
			{
				new_val = value;
			}
			else
			{
				int gx, gy;
				gx = getValue(i-sizex-1) * mask_x[0];
				gx += getValue(i-sizex) * mask_x[1];
				gx += getValue(i-sizex+1) * mask_x[2];
				gx += getValue(i-1) * mask_x[3];
				gx += getValue(i) * mask_x[4];
				gx += getValue(i+1) * mask_x[5];
				gx += getValue(i+sizex-1) * mask_x[6];
				gx += getValue(i+sizex) * mask_x[7];
				gx += getValue(i+sizex+1) * mask_x[8];
				gx /= 9;

				gy = getValue(i-sizex-1) * mask_y[0];
				gy += getValue(i-sizex) * mask_y[1];
				gy += getValue(i-sizex+1) * mask_y[2];
				gy += getValue(i-1) * mask_y[3];
				gy += getValue(i) * mask_y[4];
				gy += getValue(i+1) * mask_y[5];
				gy += getValue(i+sizex-1) * mask_y[6];
				gy += getValue(i+sizex) * mask_y[7];
				gy += getValue(i+sizex+1) * mask_y[8];
				gy /= 9;

				new_val = (int) Math.round(Math.sqrt((gx*gx + gy*gy)));
				if (new_val > 255)
				{
					new_val = 255;
				}
			}

			matrix[i] = makeColor(new_val, new_val, new_val);			

		}

		// Após a manipulaçao da matrix, é necessário criar o objecto gráfico (image) 
		image = createImage(new MemoryImageSource(sizex, sizey, matrix, 0, sizex));
		
		// Carregar a imagem no painel externo de visualização
		imagePanel.newImage(image); 

	}

	// Função de apoio que grava a imagem visualizada
	private void guardarImagem()
	{
		// Criar uma BufferedImage a partir de uma Image
		BufferedImage bi = new BufferedImage(image.getWidth(null),image.getHeight(null),BufferedImage.TYPE_INT_RGB);
		Graphics bg = bi.getGraphics();
		bg.drawImage(image, 0, 0, null);
		bg.dispose();

	    // Escrever ficheiro de saída
	    // Pq não implementar uma interface de escolha do nome?
		try {
			ImageIO.write(bi, "jpg", new File("resultado.jpg"));
		} catch (IOException e) {
			System.err.println("Couldn't write output file!");
			return;
		}
	}
	
	// Função de apoio que carrega uma imagem externa
	public void LoadImage(String fileName) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		image = toolkit.getImage(fileName);
		MediaTracker mediaTracker = new MediaTracker(this);
		mediaTracker.addImage(image, 0);
		try { mediaTracker.waitForID(0); }
		catch (InterruptedException ie) {};		
	}

	// Funções de apoio para extrair os valores de R, G e B de uma imagem.
	private int getRed(int color) { return (color >> 16) & 0xff; }
	private int getGreen(int color) { return (color >> 8) & 0xff; }
	private int getBlue(int color) { return color & 0xff; }
	private int makeColor(int red, int green, int blue) { return (255 << 24) | (red << 16) | (green << 8) | blue; }
}

//---------------------------------------------------------------
// Classe Frame de apoio para visualização de uma imagem
//--------------------------------------------------------------- 
class ImagePanel extends Frame
{
	private static final long serialVersionUID = 1L;
	private Image image; 
	
	// Construtor
	public ImagePanel(Image newImage)
	{
		image = newImage;

		// Handle close window button
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	// Carregar nova imagem no ImagePanel
	public void newImage(Image im)
	{
		image = im;
		repaint();
	}
	
	// Desenhar imagem 
	public void paint(Graphics g) {
		g.drawImage(image, 0, 0, null);
		super.paint(g);
	}
}