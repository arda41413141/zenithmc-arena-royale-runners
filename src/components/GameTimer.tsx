
import React from 'react';

interface GameTimerProps {
  timeLeft: number;
  currentPhase: string;
}

const GameTimer: React.FC<GameTimerProps> = ({ timeLeft, currentPhase }) => {
  const getTimerColor = () => {
    if (timeLeft <= 5) return 'text-red-500';
    if (timeLeft <= 10) return 'text-yellow-500';
    return 'text-green-500';
  };

  const getProgressWidth = () => {
    return (timeLeft / 30) * 100;
  };

  if (currentPhase !== 'looting') return null;

  return (
    <div className="fixed top-4 left-1/2 transform -translate-x-1/2 z-50">
      <div className="bg-black bg-opacity-80 text-white px-6 py-4 rounded-lg shadow-xl">
        <div className="text-center mb-2">
          <h3 className="text-lg font-bold">LOOT ZAMANI!</h3>
          <p className="text-sm text-gray-300">Ortadaki lootları almak için koş!</p>
        </div>
        
        <div className="flex items-center space-x-4">
          <div className={`text-4xl font-bold ${getTimerColor()} animate-pulse`}>
            {timeLeft}
          </div>
          <div className="flex-1">
            <div className="w-32 h-3 bg-gray-700 rounded-full overflow-hidden">
              <div 
                className="h-full bg-gradient-to-r from-green-500 to-red-500 transition-all duration-1000 ease-linear"
                style={{ width: `${getProgressWidth()}%` }}
              />
            </div>
            <div className="text-xs text-gray-400 mt-1">Kalan Süre</div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default GameTimer;
