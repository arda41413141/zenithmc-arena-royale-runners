
import { useState, useEffect, useCallback } from 'react';

export interface Player {
  id: string;
  name: string;
  avatar: string;
  health: number;
  isEliminated: boolean;
}

export interface Team {
  id: string;
  name: string;
  players: Player[];
  isEliminated: boolean;
  color: string;
}

export interface LootItem {
  id: string;
  name: string;
  type: 'weapon' | 'armor' | 'potion' | 'food';
  rarity: 'common' | 'rare' | 'epic' | 'legendary';
  position: { x: number; y: number };
}

type GamePhase = 'waiting' | 'running' | 'looting' | 'fighting' | 'final' | 'ended';

export const useCircleGame = () => {
  const [teams, setTeams] = useState<Team[]>([]);
  const [currentPhase, setCurrentPhase] = useState<GamePhase>('waiting');
  const [currentRound, setCurrentRound] = useState(1);
  const [timeLeft, setTimeLeft] = useState(30);
  const [lootItems, setLootItems] = useState<LootItem[]>([]);
  const [winnerTeam, setWinnerTeam] = useState<Team | null>(null);
  const [activeTeams, setActiveTeams] = useState<Team[]>([]);

  const teamColors = [
    '#FF6B6B', '#4ECDC4', '#45B7D1', '#96CEB4', '#FFEAA7',
    '#DDA0DD', '#98D8C8', '#F7DC6F', '#BB8FCE'
  ];

  const generateTeams = useCallback(() => {
    const playerNames = [
      'Steve', 'Alex', 'Notch', 'Herobrine', 'Enderman', 'Creeper',
      'Zombie', 'Skeleton', 'Spider', 'Witch', 'Villager', 'Golem',
      'Blaze', 'Ghast', 'Piglin', 'Warden', 'Ender_Dragon', 'Wither'
    ];

    const newTeams: Team[] = [];
    for (let i = 0; i < 9; i++) {
      const team: Team = {
        id: `team-${i + 1}`,
        name: `Takım ${i + 1}`,
        color: teamColors[i],
        isEliminated: false,
        players: [
          {
            id: `player-${i * 2 + 1}`,
            name: playerNames[i * 2],
            avatar: `https://minotar.net/avatar/${playerNames[i * 2]}/64.png`,
            health: 100,
            isEliminated: false
          },
          {
            id: `player-${i * 2 + 2}`,
            name: playerNames[i * 2 + 1],
            avatar: `https://minotar.net/avatar/${playerNames[i * 2 + 1]}/64.png`,
            health: 100,
            isEliminated: false
          }
        ]
      };
      newTeams.push(team);
    }
    setTeams(newTeams);
    setActiveTeams(newTeams);
  }, []);

  const generateLoot = useCallback(() => {
    const lootTypes = [
      { name: 'Diamond Sword', type: 'weapon' as const, rarity: 'legendary' as const },
      { name: 'Golden Apple', type: 'food' as const, rarity: 'epic' as const },
      { name: 'Iron Armor', type: 'armor' as const, rarity: 'rare' as const },
      { name: 'Healing Potion', type: 'potion' as const, rarity: 'common' as const },
      { name: 'Enchanted Bow', type: 'weapon' as const, rarity: 'epic' as const },
      { name: 'Shield', type: 'armor' as const, rarity: 'rare' as const }
    ];

    const newLoot: LootItem[] = [];
    const lootCount = Math.floor(Math.random() * 4) + 3; // 3-6 loot items

    for (let i = 0; i < lootCount; i++) {
      const randomLoot = lootTypes[Math.floor(Math.random() * lootTypes.length)];
      const angle = (Math.PI * 2 * i) / lootCount;
      const radius = 120 + Math.random() * 80;
      
      newLoot.push({
        id: `loot-${i}`,
        ...randomLoot,
        position: {
          x: Math.cos(angle) * radius,
          y: Math.sin(angle) * radius
        }
      });
    }
    setLootItems(newLoot);
  }, []);

  const startRound = useCallback(() => {
    if (activeTeams.length <= 2) {
      setCurrentPhase('final');
      return;
    }

    setCurrentPhase('looting');
    setTimeLeft(30);
    generateLoot();
  }, [activeTeams.length, generateLoot]);

  const eliminateTeams = useCallback(() => {
    const teamsToEliminate = Math.ceil(activeTeams.length * 0.3); // Her turda %30 eleme
    const remainingTeams = activeTeams.slice(0, activeTeams.length - teamsToEliminate);
    
    setActiveTeams(remainingTeams);
    setTeams(prev => prev.map(team => ({
      ...team,
      isEliminated: !remainingTeams.find(t => t.id === team.id)
    })));
  }, [activeTeams]);

  const endGame = useCallback((winner: Team) => {
    setWinnerTeam(winner);
    setCurrentPhase('ended');
  }, []);

  useEffect(() => {
    generateTeams();
  }, [generateTeams]);

  useEffect(() => {
    let interval: NodeJS.Timeout;

    if (currentPhase === 'looting' && timeLeft > 0) {
      interval = setInterval(() => {
        setTimeLeft(prev => prev - 1);
      }, 1000);
    } else if (currentPhase === 'looting' && timeLeft === 0) {
      setCurrentPhase('fighting');
      setTimeout(() => {
        eliminateTeams();
        setCurrentRound(prev => prev + 1);
        setTimeout(() => startRound(), 2000);
      }, 3000);
    } else if (currentPhase === 'final') {
      setTimeout(() => {
        const winner = activeTeams[Math.floor(Math.random() * activeTeams.length)];
        endGame(winner);
      }, 5000);
    }

    return () => clearInterval(interval);
  }, [currentPhase, timeLeft, eliminateTeams, startRound, activeTeams, endGame]);

  return {
    teams,
    activeTeams,
    currentPhase,
    currentRound,
    timeLeft,
    lootItems,
    winnerTeam,
    startRound,
    startGame: () => {
      setCurrentPhase('running');
      setTimeout(startRound, 2000);
    }
  };
};
