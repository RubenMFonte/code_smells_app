package projecto_es;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import com.github.javaparser.ast.CompilationUnit;

public class ListsToInterface {
	
	// Singleton instance
	public static ListsToInterface listsToInterface = null;

	private List<ClassDataStructure> dataList = new ArrayList<ClassDataStructure>();
	
	public List<ClassDataStructure> getDataList() {
		return dataList;
	}

	public void setDataList(List<ClassDataStructure> dataList) {
		this.dataList = dataList;
	}

	public static ListsToInterface getListsToInterfaceInstance()
	{
		if(listsToInterface == null) listsToInterface = new ListsToInterface();
		
		return listsToInterface;
	}
	
	public ListsToInterface()
	{
		dataList = new ArrayList<ClassDataStructure>();
	}
	
	public JList<String> getPackageJList() {
		DefaultListModel<String> listTemp = new DefaultListModel<String>();
		List<String> tempListPackage = new ArrayList<String>();
		
		for(int i=0; i<this.dataList.size(); i++) {
			tempListPackage.add(dataList.get(i).getPackageName());
		}
		
		tempListPackage = tempListPackage.stream().distinct().collect(Collectors.toList());
		
		for(int j=0; j<tempListPackage.size(); j++) {
			listTemp.addElement(tempListPackage.get(j));
		}
		
		JList<String> packageJList = new JList<String>(listTemp);
		return packageJList;
	}
	
//ligar ao clique dos packages	
	public JList<String> getClassJList() {
		DefaultListModel<String> listTemp = new DefaultListModel<String>();
		List<String> tempListClasses = new ArrayList<String>();
//criar string do package 		
		for(int i=0; i<this.dataList.size(); i++) {
			tempListClasses.add(this.dataList.get(i).getClassName());
		}
		
		tempListClasses = tempListClasses.stream().distinct().collect(Collectors.toList());
		for(int j=0; j<tempListClasses.size(); j++) {
			listTemp.addElement(tempListClasses.get(j));
		}
		
		JList<String> classJList = new JList<String>(listTemp);
		return classJList;
	}
	
	public JList<String> showClasses(String packageName) {
		DefaultListModel<String> classesNamesList = new DefaultListModel<String>();
		List<String> tempListClasses = new ArrayList<String>();
		for(int i=0; i<this.dataList.size(); i++) {
			if(dataList.get(i).getPackageName().equals(packageName)) {
				tempListClasses.add(this.dataList.get(i).getClassName());
			}
		}
		tempListClasses = tempListClasses.stream().distinct().collect(Collectors.toList());
		for(int j=0; j<tempListClasses.size(); j++) {
			classesNamesList.addElement(tempListClasses.get(j));
		}
		JList<String> classesJList = new JList<String>(classesNamesList);
		return classesJList;
		}
	
	public JList<String> getMethodsJList() {
		DefaultListModel<String> listTemp = new DefaultListModel<String>();
		
		for(int i=0; i<this.dataList.size(); i++)
			for(int j=0; j<this.dataList.get(i).getMethodDataStructureList().size(); j++) {
			listTemp.add(i, this.dataList.get(i).getMethodDataStructureList().get(j).getMethodName());
		}
		JList<String> methodsJList = new JList<String>(listTemp);
		return methodsJList;
	}
	
	public JList<String> showGeneralMetrics(GeneralStatistics generalStatistics){
		String packageNumber = "Number of Packages: " + generalStatistics.getN_package();
		String classNumber = "Number of Classes: " + generalStatistics.getN_classes();
		String methodsNumber = "Number of Methods: " + generalStatistics.getN_methods();
		String linesNumber = "Number of Lines: " + generalStatistics.getN_lines();
		
		DefaultListModel<String> statisticsNamesList = new DefaultListModel<String>();
		statisticsNamesList.addElement(packageNumber);
		statisticsNamesList.addElement(classNumber);
		statisticsNamesList.addElement(methodsNumber);
		statisticsNamesList.addElement(linesNumber);
		
		JList<String> statisticsJList = new JList<String>(statisticsNamesList);
		return statisticsJList;
	}
	
	public JList<String> showClassMetrics(String className) {
		
		DefaultListModel<String> metricsNamesList = new DefaultListModel<String>();
		for(int i=0; i<this.dataList.size(); i++) {
			if(dataList.get(i).getClassName().equals(className)) {
				String wmc_metric = "WMC_metric: " + dataList.get(i).getWMCmetric();
				String loc_metric = "LOC_metric: " + dataList.get(i).getLOCmetric();
				String nom_metric = "NOM_metric: " + dataList.get(i).getNOMmetric();
				metricsNamesList.addElement(wmc_metric);
				metricsNamesList.addElement(loc_metric);
				metricsNamesList.addElement(nom_metric);
				break;
			}
		}
		JList<String> metricsJList = new JList<String>(metricsNamesList);
		return metricsJList;
	}
	
	public JList<String> showMethodMetrics(String className, String methodName) {
		if(methodName.equals("")) {
			JList<String> methodsMetricsJList = new JList<String>();
			return methodsMetricsJList;
		}else {
			DefaultListModel<String> metricsNamesList = new DefaultListModel<String>();
			for(int i=0; i<this.dataList.size(); i++) {
					if(dataList.get(i).getClassName().equals(className))
						for(int j=0; j<dataList.get(i).getMethodDataStructureList().size(); j++) {
							if(dataList.get(i).getMethodDataStructureList().get(j).getMethodName().equals(methodName)){
								String loc_metric = "LOC_metric: " + dataList.get(i).getMethodDataStructureList().get(j).getLOCMetric();
								String cyclo_method = "Cyclo_method: " + dataList.get(i).getMethodDataStructureList().get(j).getCYCLOMetric();
								metricsNamesList.addElement(loc_metric);
								metricsNamesList.addElement(cyclo_method);
								break;
							}
						}
				}
				JList<String> methodsMetricsJList = new JList<String>(metricsNamesList);
				return methodsMetricsJList;
		}
	}  
	
	public JList<String> showMethods(String className) {
		DefaultListModel<String> methodsNamesList = new DefaultListModel<String>();
		for(int i=0; i<this.dataList.size(); i++) {
			if(dataList.get(i).getClassName().equals(className)) {
				for(int j=0; j<dataList.get(i).getMethodDataStructureList().size(); j++) {
					methodsNamesList.addElement(dataList.get(i).getMethodDataStructureList().get(j).getMethodName());
				}
					break;
				}
			}
			JList<String> methodsJList = new JList<String>(methodsNamesList);
			return methodsJList;
		}
	
	public boolean compareTwoJLists(JList<String> JL1, JList<String> JL2 ) {
		if(JL1.getModel().getSize() == JL2.getModel().getSize()) {
			for(int i=0; i<JL1.getModel().getSize(); i++) {
				if(!(JL1.getModel().getElementAt(i).equals(JL1.getModel().getElementAt(i)))) {
					return false;
				}
			}
		}else {
			return false;
		}
		return true;
	}
			

}
