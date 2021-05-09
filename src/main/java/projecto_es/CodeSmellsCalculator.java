package projecto_es;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import javax.swing.JTable;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

public class CodeSmellsCalculator {
	
	// Singleton instance
	private static CodeSmellsCalculator codeSmellsCalculator = null;

	// Class variables
	private List<ClassDataStructure> classDataStructureList;
	private List<ClassBooleanObject> classWithSpecialistValues;
	private List<Rule> activeRules;
	private List<CodeSmellStatistics> statistics;

	private CodeSmellsCalculator() {
		classDataStructureList = new ArrayList<ClassDataStructure>();
		classWithSpecialistValues = new ArrayList<ClassBooleanObject>();
		activeRules = new ArrayList<Rule>();
		statistics = new ArrayList<CodeSmellStatistics>();

	}
	
	public static CodeSmellsCalculator getCodeSmellsCalculatorInstance() {
		if (codeSmellsCalculator == null)
			codeSmellsCalculator = new CodeSmellsCalculator();

		return codeSmellsCalculator;
	}

	private void getCodeSmellsActiveRules(String filename) throws FileNotFoundException {
		File saveRule = new File(filename);
		Scanner myReader = new Scanner(saveRule);
		while (myReader.hasNextLine()) {
			String data = myReader.nextLine();
			Rule ruleData = new Rule(data);
			if (ruleData.isActive())
				activeRules.add(new Rule(data));
		}
		myReader.close();
	}

	
	public JTable fillCodeSmellTable() {
		String[] columnNames = { "Class", "is_God_Class", "Method ID", "Method Name", "is_long_method" };
		int statisticsJTNumberLines = 0;
		for(int c = 0; c < classDataStructureList.size(); c++) {
			statisticsJTNumberLines +=classDataStructureList.get(c).getMethods().size();
		}
		String[][] statisticsJT = new String[statisticsJTNumberLines][columnNames.length];
		int line = 0;
		for(int i=0; i<classDataStructureList.size(); i++) {
			ClassDataStructure data = classDataStructureList.get(i);
			for(int j = 0; j<data.getMethods().size(); j++) {
				MethodDataStructure data_mds = (MethodDataStructure) data.getMethods().get(j);
				statisticsJT[line][0] = data.getClassName();
				statisticsJT[line][1] = data.getClassClassificationDetected();
				statisticsJT[line][2] = String.valueOf(data_mds.getmethodID());
				statisticsJT[line][3] = data_mds.getMethodName();
				statisticsJT[line][4] = data_mds.getMethodClassificationDetected();
				line++;
			}
		}
		return new JTable(statisticsJT, columnNames);
	}

	// Podem alterar a assinatura do método se vos for conveniente
	private void initProcessToCalculateCodeSmellsStatistics() {
		for (Rule rule : activeRules) {
			CodeSmellStatistics css = new CodeSmellStatistics(rule.getCodeSmell(), 0, 0, 0, 0);
			if (rule.getCodeSmell().equals("Long_method")) {
				for (int i = 0; i < classDataStructureList.size(); i++) {
					if(!classDataStructureList.get(i).getClassClassificationDetected().contains("Não Detetado")) {
						for(ClassMethods method : classDataStructureList.get(i).getMethods()) {		
							MethodDataStructure cast = (MethodDataStructure) method;
							if(cast.getMethodClassificationDetected() == null) 
								calculateCodeSmellStatistics(null, cast, rule, css);										
						}
					}
				}
			}
			if (rule.getCodeSmell().equals("God_class")) {
				for (int i = 0; i < classDataStructureList.size(); i++) {
					if(classDataStructureList.get(i).getClassClassificationDetected() == null) 
						calculateCodeSmellStatistics(classDataStructureList.get(i), null, rule, css);					
				}
			}
			statistics.add(css);
		}
		System.out.println("--------------SEE STATUS----------------");
		for(CodeSmellStatistics status : statistics) {
			System.out.println(status.getCodeSmell() + " Statistics " + " VP " + status.getTrue_positive() + " FP " + status.getFalse_positive()
			+ " FN " + status.getFalse_negative() + " VN " + status.getTrue_negative());				
		}
		System.out.println("--------------END STATUS----------------");
	}

	private void calculateCodeSmellStatistics(ClassDataStructure classToDetect, MethodDataStructure methodToDetect, Rule regra, CodeSmellStatistics css) {
		List<Condition> conditionsActive = regra.getConditions();
		List<Boolean> allConditionBoolean = new ArrayList<>();
		for (int i = 0; i < conditionsActive.size(); i++) {
			int metric_value;
			if(classToDetect==null) {
				metric_value = giveMethodMetricValue(conditionsActive.get(i), methodToDetect);
			}else {
				metric_value = giveClassMetricValue(conditionsActive.get(i), classToDetect);
			}
			boolean conditionBoolean = giveConditionBooleanValue(conditionsActive.get(i), metric_value);
			allConditionBoolean.add(conditionBoolean);
		}
		boolean finalBooleanValue = allConditionBoolean.get(0);
		if (!(allConditionBoolean.size() == 1)) {
			for (int w = 0; w < regra.getLogicalOperators().size(); w++) {
				finalBooleanValue = compareConditionBooleans(finalBooleanValue, (boolean) allConditionBoolean.get(w + 1), regra.getLogicalOperators().get(w));
			}
		}
		String classification = "";
		if(classToDetect==null) {
			classification = classificationBetweenEvaluationAndSpecialist(css, finalBooleanValue, methodToDetect.getMethodCodeSmellSpecialistValue(regra.getCodeSmell()));
			methodToDetect.setMethodClassificationDetected(classification);
		}else {
			classification = classificationBetweenEvaluationAndSpecialist(css, finalBooleanValue, classToDetect.getClassCodeSmellSpecialistValue(regra.getCodeSmell()));
			classToDetect.setClassClassificationDetected(classification);
		}
	}

	private String classificationBetweenEvaluationAndSpecialist(CodeSmellStatistics sts, boolean our, boolean specialist) {
		String classification = "";
		if (our == true && specialist == true) {
			sts.increase_truePositive();
			classification = "Verdadeiro Positivo";
		}
		if (our == true && specialist == false) {
			sts.increase_falsePositive();
			classification = "Falso Positivo";
		}
		if (our == false && specialist == true) {
			sts.increase_falseNegative();
			;
			classification = "Falso Negativo";
		}
		if (our == false && specialist == false) {
			sts.increase_trueNegative();
			;
			classification = "Verdadeiro Negativo";
		}
		return classification;
	}

	private boolean compareConditionBooleans(boolean value, boolean valueToCompareWith, LogicalOperator logicalOperator) {
		switch (logicalOperator) {
		case AND:
			return value && valueToCompareWith;
		case OR:
			return value || valueToCompareWith;
		}
		return true;
	}

	private int giveClassMetricValue(Condition a, ClassDataStructure classToDetect) {
		switch (a.getMetric()) {
		case NOM_CLASS:
			return classToDetect.getNOMmetric();
		case LOC_CLASS:
			return classToDetect.getLOCmetric();
		case WMC_CLASS:
			return classToDetect.getWMCmetric();
		}
		return 0;
	}

	private int giveMethodMetricValue(Condition a, MethodDataStructure methodToDetect) {
		switch (a.getMetric()) {
		case LOC_METHOD:
			return methodToDetect.getLOCMetric();
		case CYCLO_METHOD:
			return methodToDetect.getCYCLOMetric();
		}
		return 0;
	}

	private boolean giveConditionBooleanValue(Condition a, int metric_value) {
		switch (a.getNumericOperator()) {
		case EQ:
			if (metric_value == a.getThreshold())
				return true;
			return false;
		case NE:
			if (metric_value != a.getThreshold())
				return true;
			return false;
		case GT:
			if (metric_value > a.getThreshold())
				return true;
			return false;
		case LT:
			if (metric_value < a.getThreshold())
				return true;
			return false;
		case GE:
			if (metric_value >= a.getThreshold())
				return true;
			return false;
		case LE:
			if (metric_value <= a.getThreshold())
				return true;
			return false;
		}
		return true;
	}
	
	private void defineGodClassValueFromSpecialistToClass() {
		for(ClassDataStructure ourClass : classDataStructureList) {
			try {
				ClassBooleanObject v = classWithSpecialistValues.stream().filter(cbo -> cbo.getClassName().equals(ourClass.getClassName())).findFirst().get();
				ourClass.setClassCodeSmellSpecialistValue("God_class", v.getGodC());	
				@SuppressWarnings("unchecked")
				List<MethodDataStructure> lmds = (List<MethodDataStructure>)(List<?>) ourClass.getMethods();
				@SuppressWarnings("unchecked")
				List<MethodBoolean> mb = (List<MethodBoolean>) (List<?>) v.getMethods();
				defineLongMethodValueFromSpecialistToMethod(lmds, mb);
			}catch(Exception e) {	
				System.out.println("Classe não encontrada!");
				ourClass.setClassClassificationDetected("Não Detetado - Classe Inexistente");
				for(ClassMethods method : ourClass.getMethods()) {
					MethodDataStructure method_cast = (MethodDataStructure) method;
					method_cast.setMethodClassificationDetected("Não Detetado - Devido a classe Inexistente");
				}
			}
			
		}
	}
	
	private void defineLongMethodValueFromSpecialistToMethod(List<MethodDataStructure> lmds, List<MethodBoolean> mb) {
		for(MethodDataStructure ourMethod : lmds) {
			try {
				MethodBoolean v2 = mb.stream().filter(object -> object.getMethodName().equals(ourMethod.getMethodName())).findFirst().get();
				ourMethod.setMethodCodeSmellSpecialistValue("Long_method", v2.getLmethod());			
			}catch(Exception e) {
				System.out.println("Método não encontrado!");
				ourMethod.setMethodClassificationDetected("Não Detetado - Método Inexistente");
			}
			
		}
	}
	
	
	public void run(List<ClassDataStructure> classesJasmlNos, List<ClassBooleanObject> classesJasmlProfs) throws FileNotFoundException {
		this.classDataStructureList = classesJasmlNos;
		this.classWithSpecialistValues = classesJasmlProfs;
		getCodeSmellsActiveRules("saveRule.txt");
		defineGodClassValueFromSpecialistToClass();
		initProcessToCalculateCodeSmellsStatistics();
	}
	

	public List<CodeSmellStatistics> getCodeSmellsStatistics() {
		return statistics;
	}
	

}
