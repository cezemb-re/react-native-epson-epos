import React from 'react';
import { Icon, ListItem } from '@ui-kitten/components';
import type { Device } from '../../../src/discovery';

export default ({
  item,
  ...props
}: {
  item?: Device;
  index?: number;
  onPress?: any;
}) => {
  return (
    <ListItem
      title={item?.deviceName}
      description={item?.bdAddress}
      accessoryLeft={(_props) => <Icon {..._props} name={'printer-outline'} />}
      accessoryRight={(_props) => (
        <Icon {..._props} name={'chevron-right-outline'} />
      )}
      {...props}
    />
  );
};
