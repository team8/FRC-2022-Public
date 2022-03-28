package com.palyrobotics.frc2022.config.subsystem;

import com.palyrobotics.frc2022.util.config.SubsystemConfigBase;
import com.palyrobotics.frc2022.util.control.ProfiledGains;

public class IndexerConfig extends SubsystemConfigBase {

	public double topRegularPO, regularVelocity, bottomRegularPO, reversePO, topReversePO, bottomReversePO, rightSparkPO,
			leftSparkPO, indexingTime, maxManualVelocity, indexerDriveButtonPO, flushVelocity;

	public ProfiledGains topIndexerGains, bottomIndexerGains;
}
