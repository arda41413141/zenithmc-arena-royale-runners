
import React from 'react';
import { useCircleGame } from '../hooks/useCircleGame';
import CircleArena from '../components/CircleArena';
import TeamDisplay from '../components/TeamDisplay';
import GameTimer from '../components/GameTimer';
import WinnerDisplay from '../components/WinnerDisplay';

const Index = () => {
  const {
    teams,
    activeTeams,
    currentPhase,
    currentRound,
    timeLeft,
    lootItems,
    winnerTeam,
    startGame
  } = useCircleGame();

  return (
    <div className="min-h-screen bg-gradient-to-br from-green-100 via-green-50 to-blue-50">
      <div className="container mx-auto px-4 py-8">
        {/* Header */}
        <div className="text-center mb-8">
          <h1 className="text-5xl font-bold text-gray-800 mb-2">
            ⚔️ ZenithMC Circle Savaşları ⚔️
          </h1>
          <p className="text-xl text-gray-600 mb-6">
            Minecraft tarzı battle royale oyunu - 9 takım, 2 oyuncu, tek kazanan!
          </p>
          
          {currentPhase === 'waiting' && (
            <button
              onClick={startGame}
              className="bg-green-600 hover:bg-green-700 text-white font-bold py-4 px-8 rounded-lg text-xl transform hover:scale-105 transition-all duration-200 shadow-xl"
            >
              🚀 Oyunu Başlat
            </button>
          )}
        </div>

        {/* Game Timer */}
        <GameTimer timeLeft={timeLeft} currentPhase={currentPhase} />

        {/* Main Game Area */}
        {currentPhase !== 'waiting' && (
          <div className="mb-8">
            <CircleArena 
              teams={teams}
              activeTeams={activeTeams}
              lootItems={lootItems}
              currentPhase={currentPhase}
            />
          </div>
        )}

        {/* Teams Display */}
        {teams.length > 0 && (
          <TeamDisplay teams={teams} currentRound={currentRound} />
        )}

        {/* Winner Display */}
        <WinnerDisplay 
          winnerTeam={winnerTeam}
          isVisible={currentPhase === 'ended'}
        />

        {/* Game Rules */}
        <div className="mt-12 bg-white rounded-lg shadow-lg p-6">
          <h3 className="text-2xl font-bold text-gray-800 mb-4">🎮 Oyun Kuralları</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <h4 className="font-bold text-lg text-green-700 mb-2">⚡ Temel Kurallar</h4>
              <ul className="space-y-1 text-gray-600">
                <li>• 9 takım, her takımda 2 oyuncu</li>
                <li>• Her turda ortada rastgele lootlar çıkar</li>
                <li>• Loot almak için 30 saniye koşma zamanı</li>
                <li>• Her turda takımlar elenir</li>
              </ul>
            </div>
            <div>
              <h4 className="font-bold text-lg text-red-700 mb-2">🏆 Kazanma Koşulları</h4>
              <ul className="space-y-1 text-gray-600">
                <li>• Son kalan 2 takım final savaşı yapar</li>
                <li>• 1v1 savaşta kazanan şampiyon olur</li>
                <li>• Kazanan takımın oyuncuları en üstte gösterilir</li>
                <li>• Tüm oyuncular NPCs olarak temsil edilir</li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Index;
