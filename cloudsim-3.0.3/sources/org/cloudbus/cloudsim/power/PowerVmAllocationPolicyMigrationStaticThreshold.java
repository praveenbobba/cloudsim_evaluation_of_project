/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.power;

import java.util.List;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;

/**
 * The Static Threshold (THR) VM allocation policy.
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
public class PowerVmAllocationPolicyMigrationStaticThreshold extends PowerVmAllocationPolicyMigrationAbstract {

	/** The utilization threshold. */
	private double utilizationThreshold = 1.1;

	/**
	 * Instantiates a new power vm allocation policy migration mad.
	 * 
	 * @param hostList the host list
	 * @param vmSelectionPolicy the vm selection policy
	 * @param utilizationThreshold the utilization threshold
	 */
	public PowerVmAllocationPolicyMigrationStaticThreshold(
			List<? extends Host> hostList,
			PowerVmSelectionPolicy vmSelectionPolicy,
			double utilizationThreshold) {
		super(hostList, vmSelectionPolicy);
		setUtilizationThreshold(utilizationThreshold);
	}

	/**
	 * Checks if is host over utilized.
	 * 
	 * @param _host the _host
	 * @return true, if is host over utilized
	 */
	@Override
	protected boolean isHostOverUtilized(PowerHost host) {
		addHistoryEntry(host, getUtilizationThreshold());
		double totalRequestedMips = 0;
		double totalRequestedRam = 0;
		double totalRequestedBw = 0;
		double pm_alf;
		double utilization;
		for (Vm vm : host.getVmList()) {
			totalRequestedMips += vm.getCurrentRequestedTotalMips();
			totalRequestedRam += vm.getCurrentRequestedRam();
			totalRequestedBw += vm.getCurrentRequestedBw();
		}
		double pm_lf = 70 * ( totalRequestedMips / host.getTotalMips() ) + 25 * ( totalRequestedRam / host.getRam() ) + 5 * ( totalRequestedBw / host.getBw() );
		//Log.printLine(" pmlf is " + pm_lf);
		pm_alf = get_pm_alf() ;
		if ( pm_alf > 70  ){
			utilization = ( pm_lf / get_pm_alf() );
			return utilization > getUtilizationThreshold();
		}
		else if(pm_lf > 70){
			return true ;
		}
		else
			return false ;
		 
		//Log.printLine("utilization is " + utilization);
		//return utilization > getUtilizationThreshold();
	}
	
	protected double get_pm_alf() {
		double total_pm_lf = 0;
		double pm_alf;
		for (Host host1 :  getHostList()) {
			PowerHost host = (PowerHost)host1 ;
			addHistoryEntry(host, getUtilizationThreshold());
			double totalRequestedMips = 0;
			double totalRequestedRam = 0;
			double totalRequestedBw = 0;
			for (Vm vm : host.getVmList()) {
				totalRequestedMips += vm.getCurrentRequestedTotalMips();
				totalRequestedRam += vm.getCurrentRequestedRam();
				totalRequestedBw += vm.getCurrentRequestedBw();
			}
			total_pm_lf += 70 * ( totalRequestedMips / host.getTotalMips() ) + 25 * ( totalRequestedRam / host.getRam() ) + 5 * ( totalRequestedBw / host.getBw() );
			Log.printLine("total pmlf is " + total_pm_lf);
			//total_pm_lf = 4000.0 ;
		}
		pm_alf = total_pm_lf / getHostList().size();
		Log.printLine(" pm_alf is " + pm_alf);
		return pm_alf ;
	}
	/**
	 * Sets the utilization threshold.
	 * 
	 * @param utilizationThreshold the new utilization threshold
	 */
	protected void setUtilizationThreshold(double utilizationThreshold) {
		this.utilizationThreshold = utilizationThreshold;
	}

	/**
	 * Gets the utilization threshold.
	 * 
	 * @return the utilization threshold
	 */
	protected double getUtilizationThreshold() {
		return utilizationThreshold;
	}

}
