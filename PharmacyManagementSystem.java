import java.util.*;

class Medicine {
    int id; String name; int stock; String expiry;
    boolean needPrescription; boolean isRegulated; double price;
    public Medicine(int id, String name, int stock, String expiry, boolean needPres, boolean regulated, double price){
        this.id=id; this.name=name; this.stock=stock; this.expiry=expiry;
        this.needPrescription=needPres; this.isRegulated=regulated; this.price=price;
    }
}
class Customer {
    int id; String name;
    public Customer(int id, String name){ this.id=id; this.name=name; }
}
class Pharmacist {
    String name;
    public Pharmacist(String name){ this.name=name; }
}
class Prescription {
    int id; int customerId;
    List<PrescriptionItem> items = new ArrayList<>();
    public Prescription(int id, int customerId){ this.id=id; this.customerId=customerId; }
}
class PrescriptionItem {
    int medicineId; int qty;
    public PrescriptionItem(int medicineId, int qty){ this.medicineId=medicineId; this.qty=qty; }
}
class Sale {
    int id; int customerId; String date;
    List<SaleDetail> details = new ArrayList<>();
    public Sale(int id, int customerId, String date){ this.id=id; this.customerId=customerId; this.date=date; }
}
class SaleDetail {
    int medicineId; int qty; double rate;
    public SaleDetail(int medicineId, int qty, double rate){ this.medicineId=medicineId; this.qty=qty; this.rate=rate; }
}
class PharmacyService {
    public static Map<Integer, Medicine> medicines = new HashMap<>();
    public static Map<Integer, Customer> customers = new HashMap<>();
    public static Map<Integer, Prescription> prescriptions = new HashMap<>();
    public static void displayMedicines(){
        System.out.println("\n--- Medicine List ---");
        for(Medicine m: medicines.values()){
            String expired = m.expiry.equals("01-01-2024") ? " EXPIRED!" : "";
            System.out.println(m.id+" | "+m.name+" | Stock:"+m.stock+" | Exp:"+m.expiry+expired);
        }
    }
}

public class PharmacyManagementSystem {
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        Pharmacist pharmacist = new Pharmacist("Dr. Ravi");
        PharmacyService.medicines.put(101, new Medicine(101,"Paracetamol",50,"28-12-2026",false,false,10));
        PharmacyService.medicines.put(102, new Medicine(102,"Codeine",10,"28-10-2027",true,true,150));
        PharmacyService.medicines.put(103, new Medicine(103,"Amoxicillin",5,"01-01-2024",true,false,20));
        while(true){
            System.out.println("\n==== PHARMACY MANAGEMENT ====");
            System.out.println("1. Add Medicine\n2. Add Customer\n3. Add Prescription\n4. Make Sale\n5. Display Medicines\n0. Exit");
            System.out.print("Enter choice: ");
            int ch = sc.nextInt();
            if(ch==0) break;
            if(ch==2){
                System.out.print("Customer ID: "); int id=sc.nextInt();
                sc.nextLine(); System.out.print("Name: "); String name=sc.nextLine();
                PharmacyService.customers.put(id, new Customer(id,name));
                System.out.println("Customer added.");
            } else if(ch==4){
                PharmacyService.displayMedicines();
                System.out.print("\nEnter Sale ID: "); int saleId=sc.nextInt();
                System.out.print("Customer ID: "); int custId=sc.nextInt();
                if(!PharmacyService.customers.containsKey(custId)){ System.out.println("Invalid Customer!"); continue; }
                System.out.print("No of items: "); int n=sc.nextInt();
                Sale sale = new Sale(saleId,custId,"26-10-2026");
                double total=0;
                for(int i=0;i<n;i++){
                    System.out.print("Medicine ID: "); int mid=sc.nextInt();
                    Medicine med = PharmacyService.medicines.get(mid);
                    if(med==null){ System.out.println("Invalid Medicine!"); continue; }
                    if(med.expiry.equals("01-01-2024")){ System.out.println("!! BLOCKED: "+med.name+" is EXPIRED !!"); continue; }
                    System.out.print("Quantity: "); int qty=sc.nextInt();
                    if(qty>med.stock){ System.out.println("Insufficient stock! Available: "+med.stock); continue; }
                    if(med.needPrescription){
                        System.out.print("Enter Prescription ID: "); int pid=sc.nextInt();
                        Prescription pr = PharmacyService.prescriptions.get(pid);
                        if(pr==null || pr.customerId!=custId){ System.out.println("Invalid Prescription! Blocked."); continue; }
                    }
                    sale.details.add(new SaleDetail(mid,qty,med.price));
                    med.stock-=qty; total+=qty*med.price;
                }
                Customer c = PharmacyService.customers.get(custId);
                System.out.println("\n========== SALE BILL ==========");
                System.out.println("Sale: "+saleId+" | Customer: "+c.name+" | Pharmacist: "+pharmacist.name);
                for(SaleDetail d: sale.details){
                    Medicine m = PharmacyService.medicines.get(d.medicineId);
                    String flag = m.isRegulated ? "[REGULATED] [Rx]" : "";
                    System.out.println(m.id+" "+m.name+" Qty:"+d.qty+" Rate:"+d.rate+" "+flag);
                }
                System.out.println("Total: Rs."+total);
                System.out.println("--- Updated Stock ---");
                for(SaleDetail d: sale.details){
                    Medicine m = PharmacyService.medicines.get(d.medicineId);
                    System.out.println(m.name+" -> "+m.stock);
                }
                System.out.println("===========================================");
            } else if(ch==5){
                PharmacyService.displayMedicines();
            } else if(ch==3){
                System.out.print("Prescription ID: "); int pid=sc.nextInt();
                System.out.print("Customer ID: "); int cid=sc.nextInt();
                PharmacyService.prescriptions.put(pid, new Prescription(pid,cid));
                System.out.println("Prescription added.");
            }
        }
    }
}
