import * as React from 'react';

import { StyleSheet, View, Button } from 'react-native';
import Beacon from 'react-native-help-scout-beacon';

export default function App() {
  return (
    <View style={styles.container}>
      <Button
        title="Open HS"
        onPress={() => {
          console.log('Opening Beacon');
          Beacon.open({
            beaconId: 'b4ade788-aa80-43b2-8a33-9c4b28feaf06',
            color: '#497E76', // mint turquoise
          });
        }}
      />
      <Button
        title="Login"
        onPress={() => {
          Beacon.identify({
            email: 'gabriel@betaacid.co',
          });
        }}
      />
      <Button
        title="Logout"
        onPress={() => {
          Beacon.logout();
        }}
      />
      <Button
        title="Suggest Google"
        onPress={() => {
          Beacon.suggest([
            {
              type: 'link',
              link: 'https://www.google.com/',
              label: 'Custom RN Suggestion',
            },
            {
              type: 'article',
              articleId: '632491061ec1962d58a8010e',
            },
          ]);

          Beacon.open({
            beaconId: 'b4ade788-aa80-43b2-8a33-9c4b28feaf06',
            color: '#497E76', // mint turquoise
          });
        }}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
});
