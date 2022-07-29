package webCrawler;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.*;
import javax.swing.*;
import javax.swing.table.*;

// �˻� ũ�ѷ�
public class SearchCrawler extends JFrame {
	// �޺��ڽ��� drop-down ��
	private static final String[] MAX_URLS = { "50", "100", "500", "1000" };

	// ȣ��Ʈ���� ������ ��� ����� ����
	private HashMap disallowListCache = new HashMap();

	// �˻� ���� GUI ��Ʈ��
	private JTextField startTextField;
	private JComboBox maxComboBox;
	private JCheckBox limitCheckBox;
	private JTextField logTextField;
	private JTextField searchTextField;
	private JCheckBox caseCheckBox;
	private JButton searchButton;

	// �˻� ���� ���� GUI ��Ʈ��
	private JLabel crawlingLabel2;
	private JLabel crawledLabel2;
	private JLabel toCrawlLabel2;
	private JProgressBar progressBar;
	private JLabel matchesLabel2;

	// ���� ��� ���ǿ� ����� �����ִ� ���̺�
	private JTable table;

	// ũ�Ѹ��� ���������� ���θ� ��Ÿ���� �÷���
	private boolean crawling;

	// �˻� ����� �α� ���Ͽ� ���� ���� PrintWriter
	private PrintWriter logFileWriter;

	// ���� ���� ���
	String fileRoute = "../html";

	// ������
	public SearchCrawler() {
		// Ÿ��Ʋ ����
		setTitle("Search Crawler");

		// ������ ũ�� ����
		setSize(600, 600);

		// closing �̺�Ʈ �ڵ鸵
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				actionExit();
			}
		});

		// ���� �޴� ����
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		JMenuItem fileExitMenuItem = new JMenuItem("Exit", KeyEvent.VK_X);
		fileExitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionExit();
			}
		});
		fileMenu.add(fileExitMenuItem);
		menuBar.add(fileMenu);
		setJMenuBar(menuBar);

		// �˻� �г� ����
		JPanel searchPanel = new JPanel();
		GridBagConstraints constraints;
		GridBagLayout layout = new GridBagLayout();
		searchPanel.setLayout(layout);

		JLabel startLabel = new JLabel("Start URL:");
		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets = new Insets(5, 5, 0, 0);
		layout.setConstraints(startLabel, constraints);
		searchPanel.add(startLabel);

		startTextField = new JTextField();
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.insets = new Insets(5, 5, 0, 5);
		layout.setConstraints(startTextField, constraints);
		searchPanel.add(startTextField);

		JLabel maxLabel = new JLabel("Max URLs to Crawl:");
		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets = new Insets(5, 5, 0, 0);
		layout.setConstraints(maxLabel, constraints);
		searchPanel.add(maxLabel);

		maxComboBox = new JComboBox(MAX_URLS);
		maxComboBox.setEditable(true);
		constraints = new GridBagConstraints();
		constraints.insets = new Insets(5, 5, 0, 0);
		layout.setConstraints(maxComboBox, constraints);
		searchPanel.add(maxComboBox);

		limitCheckBox = new JCheckBox("Limit crawling to Start URL site");
		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(0, 10, 0, 0);
		layout.setConstraints(limitCheckBox, constraints);
		searchPanel.add(limitCheckBox);

		JLabel blankLabel = new JLabel();
		constraints = new GridBagConstraints();
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		layout.setConstraints(blankLabel, constraints);
		searchPanel.add(blankLabel);

		JLabel logLabel = new JLabel("Matches Log File:");
		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets = new Insets(5, 5, 0, 0);
		layout.setConstraints(logLabel, constraints);
		searchPanel.add(logLabel);

		String file = System.getProperty("user.dir") + System.getProperty("file.separator") + "crawler.log";
		logTextField = new JTextField(file);
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.insets = new Insets(5, 5, 0, 5);
		layout.setConstraints(logTextField, constraints);
		searchPanel.add(logTextField);

		JLabel searchLabel = new JLabel("Search String:");
		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets = new Insets(5, 5, 0, 0);
		layout.setConstraints(searchLabel, constraints);
		searchPanel.add(searchLabel);

		searchTextField = new JTextField();
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(5, 5, 0, 0);
		constraints.gridwidth = 2;
		constraints.weightx = 1.0d;
		layout.setConstraints(searchTextField, constraints);
		searchPanel.add(searchTextField);

		caseCheckBox = new JCheckBox("Case Sensitive");
		constraints = new GridBagConstraints();
		constraints.insets = new Insets(5, 5, 0, 5);
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		layout.setConstraints(caseCheckBox, constraints);
		searchPanel.add(caseCheckBox);

		searchButton = new JButton("Search");
		searchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionSearch();
			}
		});
		constraints = new GridBagConstraints();
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.insets = new Insets(5, 5, 5, 5);
		layout.setConstraints(searchButton, constraints);
		searchPanel.add(searchButton);

		JSeparator separator = new JSeparator();
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.insets = new Insets(5, 5, 5, 5);
		layout.setConstraints(separator, constraints);
		searchPanel.add(separator);

		JLabel crawlingLabel1 = new JLabel("Crawling:");
		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets = new Insets(5, 5, 0, 0);
		layout.setConstraints(crawlingLabel1, constraints);
		searchPanel.add(crawlingLabel1);

		crawlingLabel2 = new JLabel();
		crawlingLabel2.setFont(crawlingLabel2.getFont().deriveFont(Font.PLAIN));
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.insets = new Insets(5, 5, 0, 5);
		layout.setConstraints(crawlingLabel2, constraints);
		searchPanel.add(crawlingLabel2);

		JLabel crawledLabel1 = new JLabel("Crawled URLs:");
		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets = new Insets(5, 5, 0, 0);
		layout.setConstraints(crawledLabel1, constraints);
		searchPanel.add(crawledLabel1);

		crawledLabel2 = new JLabel();
		crawledLabel2.setFont(crawledLabel2.getFont().deriveFont(Font.PLAIN));
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.insets = new Insets(5, 5, 0, 5);
		layout.setConstraints(crawledLabel2, constraints);
		searchPanel.add(crawledLabel2);

		JLabel toCrawlLabel1 = new JLabel("URLs to Crawl:");
		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets = new Insets(5, 5, 0, 0);
		layout.setConstraints(toCrawlLabel1, constraints);
		searchPanel.add(toCrawlLabel1);

		toCrawlLabel2 = new JLabel();
		toCrawlLabel2.setFont(toCrawlLabel2.getFont().deriveFont(Font.PLAIN));
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.insets = new Insets(5, 5, 0, 5);
		layout.setConstraints(toCrawlLabel2, constraints);
		searchPanel.add(toCrawlLabel2);

		JLabel progressLabel = new JLabel("Crawling Progress:");
		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets = new Insets(5, 5, 0, 0);
		layout.setConstraints(progressLabel, constraints);
		searchPanel.add(progressLabel);

		progressBar = new JProgressBar();
		progressBar.setMinimum(0);
		progressBar.setStringPainted(true);
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.insets = new Insets(5, 5, 0, 5);
		layout.setConstraints(progressBar, constraints);
		searchPanel.add(progressBar);

		JLabel matchesLabel1 = new JLabel("Search Matches:");
		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets = new Insets(5, 5, 10, 0);
		layout.setConstraints(matchesLabel1, constraints);
		searchPanel.add(matchesLabel1);

		matchesLabel2 = new JLabel();
		matchesLabel2.setFont(matchesLabel2.getFont().deriveFont(Font.PLAIN));
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.insets = new Insets(5, 5, 10, 5);
		layout.setConstraints(matchesLabel2, constraints);
		searchPanel.add(matchesLabel2);

		// �˻� ��� ���̺� ����
		table = new JTable(new DefaultTableModel(new Object[][] {}, new String[] { "URL" }) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		});

		// �˻� ��� �г� ����
		JPanel matchesPanel = new JPanel();
		matchesPanel.setBorder(BorderFactory.createTitledBorder("Matches"));
		matchesPanel.setLayout(new BorderLayout());
		matchesPanel.add(new JScrollPane(table), BorderLayout.CENTER);

		// ȭ�鿡 ����ϱ� ���� �г��� �����̳ʿ� �߰�
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(searchPanel, BorderLayout.NORTH);
		getContentPane().add(matchesPanel, BorderLayout.CENTER);
	}

	// ���α׷� ����
	private void actionExit() {
		System.exit(0);
	}

	// �˻�.���� ��ư Ŭ��
	private void actionSearch() {

		// htms ���丮 ����
		mkFolder();

		// ���� ��ư�� Ŭ���Ǹ� crawling �÷��׸� ����
		if (crawling) {
			crawling = false;
			return;
		}

		ArrayList errorList = new ArrayList();

		// ���� �������� �ԷµǾ����� Ȯ��
		String startUrl = startTextField.getText().trim();
		if (startUrl.length() < 1) {
			errorList.add("Missing Start URL.");
		}
		// ���� ������ URL�� ����
		else if (verifyUrl(startUrl) == null) {
			errorList.add("Invalid Start URL.");
		}

		// �ִ� ũ�Ѹ��� URL�� ���� Ȯ��
		int maxUrls = 0;
		String max = ((String) maxComboBox.getSelectedItem()).trim();
		if (max.length() > 0) {
			try {
				maxUrls = Integer.parseInt(max);
			} catch (NumberFormatException e) {
			}
			if (maxUrls < 1) {
				errorList.add("Invalid Max URLs value.");
			}
		}

		// �˻� ��� �α� ������ �ԷµǾ����� Ȯ��
		String logFile = logTextField.getText().trim();
		if (logFile.length() < 1) {
			errorList.add("Missing Matches Log File.");
		}

		// �˻�� �ԷµǾ����� Ȯ��
		String searchString = searchTextField.getText().trim();
		if (searchString.length() < 1) {
			errorList.add("Missing Search String.");
		}

		// ������ �ִٸ� ����ϰ� ����
		if (errorList.size() > 0) {
			StringBuffer message = new StringBuffer();

			// �߻��� �������� �ϳ��� �޽����� ��ģ��
			for (int i = 0; i < errorList.size(); i++) {
				message.append(errorList.get(i));
				if (i + 1 < errorList.size()) {
					message.append("\n");
				}
			}

			showError(message.toString());
			return;
		}

		// ���� ���������� URL���� "www" ���ڿ� ����
		startUrl = removeWwwFromUrl(startUrl);

		// �˻� ũ�ѷ� ����
		search(logFile, startUrl, maxUrls, searchString);
	}

	// �˻� ����
	private void search(final String logFile, final String startUrl, final int maxUrls, final String searchString) {
		// �� �����带 �����Ͽ� �˻� ����
		Thread thread = new Thread(new Runnable() {
			public void run() {
				// ũ�Ѹ��� ����Ǵ� ���� �𷡽ð�� Ŀ���� �ٲ۴�.
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

				// �˻� ���� ��Ʈ�� ��Ȱ��ȭ
				startTextField.setEnabled(false);
				maxComboBox.setEnabled(false);
				limitCheckBox.setEnabled(false);
				logTextField.setEnabled(false);
				searchTextField.setEnabled(false);
				caseCheckBox.setEnabled(false);

				// �˻� ��ư�� �ؽ�Ʈ�� ������ ����
				searchButton.setText("Stop");

				// �˻� ���� �缳��
				table.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "URL" }) {
					public boolean isCellEditable(int row, int column) {
						return false;
					}
				});
				updateStats(startUrl, 0, 0, maxUrls);

				// �˻� ��� �α� ������ ����
				try {
					logFileWriter = new PrintWriter(new FileWriter(logFile));
				} catch (Exception e) {
					showError("Unable to open matches log file.");
					return;
				}

				// crawling �÷��׸� �Ҵ�
				crawling = true;

				// ������ ũ�Ѹ��� �����ϴ� �޼ҵ�
				crawl(startUrl, maxUrls, limitCheckBox.isSelected(), searchString, caseCheckBox.isSelected());

				// crawling �÷��׸� ����
				crawling = false;

				// �˻� ��� �α� ������ �ݴ´�
				try {
					logFileWriter.close();
				} catch (Exception e) {
					showError("Unable to close matches log file.");
				}

				// �˻��� �Ϸ�Ǿ����� ǥ��
				crawlingLabel2.setText("Done");

				// �˻� ���� ��Ʈ���� Ȱ��ȭ
				startTextField.setEnabled(true);
				maxComboBox.setEnabled(true);
				limitCheckBox.setEnabled(true);
				logTextField.setEnabled(true);
				searchTextField.setEnabled(true);
				caseCheckBox.setEnabled(true);

				// �˻� ��ư�� �ؽ�Ʈ�� �˻����� ����
				searchButton.setText("Search");

				// �⺻ Ŀ���� �ǵ�����
				setCursor(Cursor.getDefaultCursor());

				// �˻� ����� �ϳ��� ������ �ʾ����� �˸���
				if (table.getRowCount() == 0) {
					JOptionPane.showMessageDialog(SearchCrawler.this,
							"Your Search String was not found. Please try another.", "Search String Not Found",
							JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		thread.start();
	}

	// ���� ���
	private void showError(String message) {
		JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
	}

	// �˻� ���� ����
	private void updateStats(String crawling, int crawled, int toCrawl, int maxUrls) {
		crawlingLabel2.setText(crawling);
		crawledLabel2.setText("" + crawled);
		toCrawlLabel2.setText("" + toCrawl);

		// ���α׷��� �� ����
		if (maxUrls == -1) {
			progressBar.setMaximum(crawled + toCrawl);
		} else {
			progressBar.setMaximum(maxUrls);
		}
		progressBar.setValue(crawled);

		matchesLabel2.setText("" + table.getRowCount());
	}

	// �˻� ����� ���̺�� �α� ���Ͽ� �߰�
	private void addMatch(String url) {
		// �˻� ��� URL�� ���̺� �߰�
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		model.addRow(new Object[] { url });

		// �˻� ��� URL�� �α� ���Ͽ� �߰�
		try {
			logFileWriter.println(url);
		} catch (Exception e) {
			showError("Unable to log match.");
		}
	}

	// URL ���� ����
	private URL verifyUrl(String url) {
		// HTTP(s) URL�� ���
		if (!(url.toLowerCase().startsWith("http://") || url.toLowerCase().startsWith("https://")))
			return null;

		// URL ���� ����

		URL verifiedUrl = null;
		try {
			verifiedUrl = new URL(url);
		} catch (Exception e) {
			return null;
		}

		return verifiedUrl;
	}

	// �ش� URL�� ���� �κ��� ������ ����Ǵ��� �˻�
	private boolean isRobotAllowed(URL urlToCheck) {
		String host = urlToCheck.getHost().toLowerCase();

		// �ش� ȣ��Ʈ�� ���� ���� ��� ����� �̹� ����Ǿ� �ִ��� �˻�
		ArrayList disallowList = (ArrayList) disallowListCache.get(host);

		// ���� ��� ����� ���ٸ� �ٿ�ε� �Ͽ� ����
		if (disallowList == null) {
			disallowList = new ArrayList();

			try {
				URL robotsFileUrl = new URL("http://" + host + "/robots.txt");

				// �κ� ������ ����
				BufferedReader reader = new BufferedReader(new InputStreamReader(robotsFileUrl.openStream()));

				// �κ� ������ �о ���� ��� ����� �����
				String line;
				while ((line = reader.readLine()) != null) {
					if (line.indexOf("Disallow:") == 0) {
						String disallowPath = line.substring("Disallow:".length());

						// ���� ���� ��ο� �ּ��� ���ԵǾ� �ִٸ� �����Ѵ�
						int commentIndex = disallowPath.indexOf("#");
						if (commentIndex != -1) { // ã�� ���� ��쿡�� -1 ��ȯ
							disallowPath = disallowPath.substring(0, commentIndex);
						}

						// ���� ���� ����
						disallowPath = disallowPath.trim();

						// ���� ��Ͽ� ��� �߰�
						disallowList.add(disallowPath);
					}
				}

				// ȣ��Ʈ�� ���� ��� ��Ͽ� ����
				disallowListCache.put(host, disallowList);
			} catch (Exception e) {
				/* �κ� ������ ���� �� ���ܰ� �߻��ϹǷ� ������ ����� ������ �����Ѵ� */
				return true;
			}
		}

		/* ���� ��� ����� ��ȸ�ϸ鼭 �ش� URL�� ���� ������ �����Ǿ����� �˻� */
		String file = urlToCheck.getFile();
		for (int i = 0; i < disallowList.size(); i++) {
			String disallow = (String) disallowList.get(i);
			if (file.startsWith(disallow)) {
				return false;
			}
		}

		return true;
	}

	// �ش� URL�� ���� �������� �ٿ�ε�
	private String downloadPage(URL pageUrl) {
		try {
			// �ش� URL�� ����
			BufferedReader reader = new BufferedReader(new InputStreamReader(pageUrl.openStream()));

			// ���۷� �������� �о����
			String line;
			StringBuffer pageBuffer = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				pageBuffer.append(line);
			}

			// ������ ���� �ٿ�ε�
			makeHtmlFile(pageUrl.toString(), pageBuffer.toString());

			return pageBuffer.toString();
		} catch (Exception e) {
		}

		return null;
	}

	// URL���� "WWW" ���ڿ� ����
	private String removeWwwFromUrl(String url) {
		int index = url.indexOf("://www.");
		if (index != -1) {
			return url.substring(0, index + 3) + url.substring(index + 7);
		}

		return (url);
	}

	// ������ ������ �Ľ��Ͽ� ��ũ ������ �����´�
	private ArrayList retrieveLinks(URL pageUrl, String pageContents, HashSet crawledList, boolean limitHost) {

		// ��ũ�� ��Ÿ���� ���Խ� ���� ��
		Pattern p = Pattern.compile("<a\\s+href\\s*=\\s*\"?(.*?)[\"|>]", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(pageContents);

		// ��ũ ����Ʈ�� �����
		ArrayList linkList = new ArrayList();
		while (m.find()) {
			String link = m.group(1).trim();

			// �� ��ũ ����
			if (link.length() < 1) {
				continue;
			}

			// ������ ��Ŀ������ ������ ��ũ ����
			if (link.charAt(0) == '#') {
				continue;
			}

			// ���� ��ũ��Ʈ ��ũ ����
			if (link.indexOf("mailto:") != -1) {
				continue;
			}

			// �ڹ� ��ũ��Ʈ ��ũ ����
			if (link.toLowerCase().indexOf("javascript") != -1) {
				continue;
			}

			// ���� ��� �� ��� ��θ� ������ ��ũ�� �ٲ۴�
			if (link.indexOf("://") == -1) {
				// URL ���� ��� ó��
				if (link.charAt(0) == '/') {
					link = "http://" + pageUrl.getHost() + link;
					// URL ��� ��� ó��
				} else {
					String file = pageUrl.getFile();
					if (file.indexOf('/') == -1) {
						link = "http://" + pageUrl.getHost() + "/" + link;
					} else {
						String path = file.substring(0, file.lastIndexOf('/') + 1);
						link = "http://" + pageUrl.getHost() + path + link;
					}
				}
			}

			// ��ũ���� ��Ŀ�� ����
			int index = link.indexOf('#');
			if (index != -1) {
				link = link.substring(0, index);
			}

			// ��ũ���� "www" ���ڿ� ����
			link = removeWwwFromUrl(link);

			// ��ũ�� �����ϰ� ��ȿ���� �ʴٸ� �����Ѵ�
			URL verifiedLink = verifyUrl(link);
			if (verifiedLink == null) {
				continue;
			}

			/* �˻� ������ ���� �������� �����ϴ� ���. */
			if (limitHost && !pageUrl.getHost().toLowerCase().equals(verifiedLink.getHost().toLowerCase())) {
				continue;
			}

			// �̹� ũ�Ѹ��� ��ũ��� ����
			if (crawledList.contains(link)) {
				continue;
			}

			// ����Ʈ�� ��ũ�� �߰�
			linkList.add(link);
		}

		return (linkList);
	}

	/* �ش� ������ ���뿡 ����ڰ� �Է��� �˻�� �ִ��� �˻� */
	private boolean searchStringMatches(String pageContents, String searchString, boolean caseSensitive) {
		String searchContents = pageContents;

		/* ��ҹ��ڸ� �������� �ʴ´ٸ� �񱳸� ���� ������ ������ ���� �ҹ��ڷ� ���� */
		if (!caseSensitive) {
			searchContents = pageContents.toLowerCase();
		}

		// �˻�� ������ �ܾ��� ������
		Pattern p = Pattern.compile("[\\s]+");
		String[] terms = p.split(searchString);

		// �� �ܾ �ش��ϴ� ����� �ִ��� �˻�
		for (int i = 0; i < terms.length; i++) {
			if (caseSensitive) {
				if (searchContents.indexOf(terms[i]) == -1) {
					return false;
				}
			} else {
				if (searchContents.indexOf(terms[i].toLowerCase()) == -1) {
					return false;
				}
			}
		}

		return true;
	}

	// �Էµ� �˻�� ���� ������ ũ�Ѹ��� �����ϴ� �޼ҵ�
	public void crawl(String startUrl, int maxUrls, boolean limitHost, String searchString, boolean caseSensitive) {
		// ũ�Ѹ� �۾��� �ʿ��� ����Ʈ ����
		HashSet crawledList = new HashSet();
		LinkedHashSet toCrawlList = new LinkedHashSet();

		// ũ�Ѹ��� ����Ʈ�� ���� �ǿ��� URL �߰�
		toCrawlList.add(startUrl);

		/* ũ�Ѹ��� ����Ʈ�� ��ȸ�ϸ鼭 ũ�Ѹ� ���� */
		while (crawling && toCrawlList.size() > 0) {
			/* ũ�Ѹ��� �ִ� URL ������ �����ߴ��� �˻� */
			if (maxUrls != -1) {
				if (crawledList.size() == maxUrls) {
					break;
				}
			}

			// ũ�Ѹ��� ����Ʈ���� ���� ó���� URL�� ����
			String url = (String) toCrawlList.iterator().next();
			// ũ�Ѹ��� ����Ʈ���� URL ����
			toCrawlList.remove(url);

			// ��Ʈ�� url�� URL ��ü�� ��ȯ
			URL verifiedUrl = verifyUrl(url);

			// URL�� ������ ��ζ�� ó������ �ʴ´�
			if (!isRobotAllowed(verifiedUrl)) {
				continue;
			}

			// �˻� ���� ����
			updateStats(url, crawledList.size(), toCrawlList.size(), maxUrls);

			// URL�� ũ�Ѹ��� ��Ͽ� �߰�
			crawledList.add(url);

			// URL�� ����Ű�� �������� �ٿ�ε�
			String pageContents = downloadPage(verifiedUrl);

			/* �������� �ٿ�ε尡 �����ߴٸ�, �ش� �������� ��� ��ũ�� �̾Ƴ��� �������� ������ �˻�� �����ϴ��� �˻��Ѵ�. */
			if (pageContents != null && pageContents.length() > 0) {
				// ���������� ��ȿ�� ��ũ ����� �̴´�
				ArrayList links = retrieveLinks(verifiedUrl, pageContents, crawledList, limitHost);

				// ũ�Ѹ��� ����Ʈ�� ��ũ �߰�
				toCrawlList.addAll(links);

				/* �������� �˻�� �ִٸ� �˻� ����� �߰� */
				if (searchStringMatches(pageContents, searchString, caseSensitive)) {
					addMatch(url);
				}
			}

			// �˻� ���� ����
			updateStats(url, crawledList.size(), toCrawlList.size(), maxUrls);
		}
	}

	// html ���� ����
	void makeHtmlFile(String fileName, String contents) throws MalformedURLException {

		// ���� �̸��� Ư�����ڴ� ��� �Ұ������� _�� ����
		String saveFileName = fileName.toString().replace("/", "_");
		saveFileName = saveFileName.toString().replace("\\", "_");
		saveFileName = saveFileName.toString().replace("?", "_");
		saveFileName = saveFileName.toString().replace("*", "_");
		saveFileName = saveFileName.toString().replace(":", "_");
		saveFileName = saveFileName.toString().replace(";", "_");
		saveFileName = saveFileName.toString().replace("|", "_");
		saveFileName = saveFileName.toString().replace("\"", "_");

		// ������ ������ ������
		String savePageContents = contents;

		// url��ũ�� �ٿ���� ���Ϸ� ��ü
		Pattern p = Pattern.compile("<a.*?\\s+href\\s*=\\s*\"?(.*?)[\"|>]", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(contents);
		while (m.find()) {
			String link = m.group(1).trim();
			String beforeLink = link;

			// ���� ��� �� ��� ��θ� ������ ��ũ�� �ٲ۴�
			URL pageUrl = new URL(fileName);
			if (link.indexOf("://") == -1) {
				// URL ���� ��� ó��
				if (link.charAt(0) == '/') {
					link = "http://" + pageUrl.getHost() + link;
					// URL ��� ��� ó��
				} else {
					String file = pageUrl.getFile();
					if (file.indexOf('/') == -1) {
						link = "http://" + pageUrl.getHost() + "/" + link;
					} else {
						String path = file.substring(0, file.lastIndexOf('/') + 1);
						link = "http://" + pageUrl.getHost() + path + link;
					}
				}
			}
			link = link.toString().replace("/", "_");
			link = link.toString().replace("\\", "_");
			link = link.toString().replace("?", "_");
			link = link.toString().replace("*", "_");
			link = link.toString().replace(":", "_");
			link = link.toString().replace(";", "_");
			link = link.toString().replace("|", "_");
			link = link.toString().replace("\"", "_");

			savePageContents = savePageContents.replaceAll(beforeLink, link + ".html");
			System.out.println(link);
		}

		// ���� ���
		try {
			OutputStream output = new FileOutputStream("../htmls/" + saveFileName + ".html");
			byte[] by = savePageContents.getBytes();
			output.write(by);
		} catch (Exception e) {
			e.printStackTrace();
			e.getStackTrace();
		}
	}

	// ���� ����
	void mkFolder() {
		// ���� ��ü ����
		String path = "../htmls"; // ���� ���
		File Folder = new File(path);

		// ���丮 ������ ����
		if (!Folder.exists()) {
			try {
				Folder.mkdir();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// ���� ���� ���� ����
		File[] folder_list = Folder.listFiles(); // ���ϸ���Ʈ ������
		for (int j = 0; j < folder_list.length; j++) {
			folder_list[j].delete(); // ���� ����
		}
	}

	// ���� �޼ҵ�
	public static void main(String[] args) {
		SearchCrawler crawler = new SearchCrawler();
		crawler.show();
	}
}
