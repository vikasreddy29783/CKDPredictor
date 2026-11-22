import java.util.*;
public class CKDPredictor {

    static final String[] FEATURES = {"age","bp","sg","al","su","bgr","bu","sc","hemo","pcv","htn","dm","appet","pe","ane"};
    static final String[] LABELS = {
        "Age (years)", "Blood pressure (mm Hg)", "Specific gravity (1.005-1.025)",
        "Albumin (0-5)", "Sugar (0-5)", "Blood glucose random (mg/dL)",
        "Blood urea (mg/dL)", "Serum creatinine (mg/dL)", "Hemoglobin (g/dL)",
        "Packed cell volume", "Hypertension (yes/no)", "Diabetes mellitus (yes/no)",
        "Appetite (good/poor)", "Pedal edema (yes/no)", "Anemia (yes/no)"};
    // Features at index >= 10 are yes/no style; "positive" word for each:
    static final String[] POSITIVE = {"yes","yes","poor","yes","yes"};

    static final double[] MEAN = {51.36273,78.8697,1.01618,1.27167,0.49,132.54112,49.96982,2.52889,12.65094,38.98305,0.27167,0.22,0.21167,0.16667,0.155};
    static final double[] STD  = {14.66157,12.1355,0.00695,1.48027,0.7637,52.68671,34.7602,2.60194,3.08613,9.28077,0.44482,0.41425,0.40849,0.37268,0.3619};
    static final double[] COEF = {0.29081,0.34226,-1.16505,1.09368,0.57491,0.54008,0.41989,0.55158,-0.93521,-0.85501,0.48753,0.39379,0.34344,0.46902,0.39864};
    static final double INTERCEPT = 0.98805;

    static double sigmoid(double z) { return 1.0 / (1.0 + Math.exp(-z)); }

    /** Returns probability of CKD; fills contributions[] for explanation. */
    static double predict(double[] x, double[] contributions) {
        double z = INTERCEPT;
        for (int i = 0; i < x.length; i++) {
            contributions[i] = COEF[i] * (x[i] - MEAN[i]) / STD[i];
            z += contributions[i];
        }
        return sigmoid(z);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        double[] x = new double[FEATURES.length];

        System.out.println("=== Chronic Kidney Disease Risk Predictor ===");
        System.out.println("(Demo model, educational use only - not a medical diagnosis)\n");

        for (int i = 0; i < FEATURES.length; i++) {
            while (true) {
                System.out.print(LABELS[i] + ": ");
                String s = sc.nextLine().trim().toLowerCase();
                try {
                    if (i >= 10) {
                        String pos = POSITIVE[i - 10];
                        if (!s.equals("yes") && !s.equals("no") && !s.equals("good") && !s.equals("poor"))
                            throw new NumberFormatException();
                        x[i] = s.equals(pos) ? 1 : 0;
                    } else {
                        x[i] = Double.parseDouble(s);
                    }
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("  Invalid input, please try again.");
                }
            }
        }

        double[] contrib = new double[x.length];
        double p = predict(x, contrib);

        System.out.println("\n--- Result ---");
        System.out.printf("Probability of CKD: %.1f%%%n", p * 100);
        System.out.println(p >= 0.5 ? "HIGH risk of CKD. Please consult a nephrologist."
                                    : "LOW risk of CKD.");

        Integer[] idx = new Integer[x.length];
        for (int i = 0; i < idx.length; i++) idx[i] = i;
        Arrays.sort(idx, (a, b) -> Double.compare(Math.abs(contrib[b]), Math.abs(contrib[a])));
        System.out.println("\nTop factors (+ raises risk, - lowers risk):");
        for (int k = 0; k < 5; k++)
            System.out.printf("  %-30s %+.2f%n", LABELS[idx[k]], contrib[idx[k]]);
    }
}