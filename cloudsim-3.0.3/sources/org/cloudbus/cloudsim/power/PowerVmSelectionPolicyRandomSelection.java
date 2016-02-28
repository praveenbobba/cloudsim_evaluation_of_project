/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.power;

import java.util.List;


import org.cloudbus.cloudsim.Vm;



/**
 * The Minimum Utilization (MU) VM selection policy.
 * 
 * If you are using any algorithms, policies or workload included in the power package, please cite
 * the following paper:
 * 
 * Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive
 * Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in
 * Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24,
 * Issue 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012
 * 
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 3.0
 */
public class PowerVmSelectionPolicyRandomSelection extends PowerVmSelectionPolicy {

	/*
	 * (non-Javadoc)
	 * @see
	 * org.cloudbus.cloudsim.experiments.power.PowerVmSelectionPolicy#getVmsToMigrate(org.cloudbus
	 * .cloudsim.power.PowerHost)
	 */
	@Override
	public Vm getVmToMigrate(PowerHost host) {
		List<PowerVm> migratableVms = getMigratableVms(host);
		if (migratableVms.isEmpty()) {
			return null;
		}
		Vm vmToMigrate = null;
		double vm_alf = get_vm_alf(host) ;
		double min_vm_lf = Double.MAX_VALUE;
		for (Vm vm : migratableVms) {
			if (vm.isInMigration()) {
				continue;
			}
			double vm_lf = 70 * (vm.getCurrentRequestedTotalMips() / host.getTotalMips()) + 25 * (vm.getCurrentRequestedRam() / host.getRam()) + 5 * ( vm.getCurrentRequestedBw() / host.getBw() );
			if (vm_lf > vm_alf && vm_lf < min_vm_lf) {
				min_vm_lf = vm_lf;
				vmToMigrate = vm;
			}
		}
		return vmToMigrate;
	}
	
	protected double get_vm_alf(PowerHost host) {
		double total_vm_lf = 0;
		double vm_alf = 0;
		double hostMips = host.getTotalMips() ;
		double hostRam = host.getRam() ;
		double hostBw = host.getBw() ;
		double vmCount = 0 ;
			//addHistoryEntry(host, getUtilizationThreshold());

			for (Vm vm : host.getVmList()) {
				vmCount += 1 ;
				total_vm_lf += 70 * (vm.getCurrentRequestedTotalMips() / hostMips ) + 25 * (vm.getCurrentRequestedRam() / hostRam ) + 5 * ( vm.getCurrentRequestedBw() / hostBw );
			}
			//total_vm_lf += 70 * ( totalRequestedMips / host.getTotalMips() ) + 25 * ( totalRequestedRam / host.getRam() ) + 5 * ( totalRequestedBw / host.getBw() );
			//Log.printLine("total pmlf is " + total_pm_lf);
			//total_pm_lf = 4000.0 ;
		
		vm_alf = total_vm_lf / vmCount;
		//Log.printLine(" pm_alf is " + pm_alf);
		return vm_alf ;
	}

}
